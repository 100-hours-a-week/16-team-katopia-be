package katopia.fitcheck.service.search;

import katopia.fitcheck.global.aop.SearchLog;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.dto.post.response.PostSummary;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.repository.post.PostSummaryProjection;
import katopia.fitcheck.dto.search.PostSearchResponse;
import katopia.fitcheck.dto.search.MemberSearchResponse;
import katopia.fitcheck.dto.search.MemberSearchSummary;
import katopia.fitcheck.global.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MemberRepository memberRepository;
    private final MemberFollowRepository memberFollowRepository;
    private final PostRepository postRepository;
    private final SearchValidator searchValidator;
    @Transactional(readOnly = true)
    @katopia.fitcheck.global.aop.SearchLog("users")
    public MemberSearchResponse searchUsers(
                                          Long requesterId,
                                          String query,
                                          String sizeValue,
                                          String after) {
        String keyword = LikeEscapeHelper.escape(searchValidator.requireQuery(query));
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Member> members = loadUsers(keyword, size, after);
        List<Long> targetIds = members.stream().map(Member::getId).toList();
        List<MemberSearchSummary> summaries = toMemberSummaries(requesterId, members, targetIds);
        String nextCursor = resolveNextCursor(members, size);
        return MemberSearchResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    @SearchLog("posts")
    public PostSearchResponse searchPosts(String query,
                                          String sizeValue,
                                          String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<PostSummary> summaries = loadPostSummaries(query, size, after);
        String nextCursor = resolveNextCursor(summaries, size);
        return PostSearchResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    @SearchLog("posts-fulltext")
    public PostSearchResponse searchPostsFulltext(String query, String sizeValue) {
        String keyword = searchValidator.requireQuery(query, Policy.SEARCH_MAX_FULLTEXT_QUERY_LENGTH);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<PostSummaryProjection> posts = postRepository.searchLatestByContentFulltextSummary(
                keyword,
                AccountStatus.ACTIVE.name(),
                size
        );
        List<PostSummary> summaries = posts.stream()
                .map(this::toSummary)
                .toList();
        return PostSearchResponse.of(summaries, null);
    }

    private List<Member> loadUsers(String nickname, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (!StringUtils.hasText(after)) {
            return memberRepository.searchLatestByNickname(
                    nickname,
                    AccountStatus.ACTIVE,
                    pageRequest
            );
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return memberRepository.searchPageAfterByNickname(
                nickname,
                AccountStatus.ACTIVE,
                cursor.createdAt(),
                cursor.id(),
                pageRequest
        );
    }

    private List<MemberSearchSummary> toMemberSummaries(Long requesterId, List<Member> members, List<Long> targetIds) {
        if (members.isEmpty()) {
            return List.of();
        }
        if (requesterId == null) {
            return members.stream()
                    .map(member -> MemberSearchSummary.of(member, false))
                    .toList();
        }
        var followedIds = memberFollowRepository.findFollowedIds(requesterId, targetIds);
        return members.stream()
                .map(member -> MemberSearchSummary.of(member, followedIds.contains(member.getId())))
                .toList();
    }

    private List<PostSummary> loadPostSummaries(String query, int size, String after) {
        boolean tagOnly = query.startsWith("#");
        String keyword = tagOnly ? query.substring(1).trim() : query;
        keyword = LikeEscapeHelper.escape(searchValidator.requireQuery(keyword));
        PageRequest pageRequest = PageRequest.of(0, size);
        List<PostSummaryProjection> posts;
        if (!StringUtils.hasText(after)) {
            posts = tagOnly
                    ? postRepository.searchLatestByTagSummary(keyword, AccountStatus.ACTIVE, pageRequest)
                    : postRepository.searchLatestByContentSummary(keyword, AccountStatus.ACTIVE, pageRequest);
        } else {
            CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
            posts = tagOnly
                    ? postRepository.searchPageAfterByTagSummary(keyword, AccountStatus.ACTIVE, cursor.createdAt(), cursor.id(), pageRequest)
                    : postRepository.searchPageAfterByContentSummary(keyword, AccountStatus.ACTIVE, cursor.createdAt(), cursor.id(), pageRequest);
        }
        return posts.stream().map(this::toSummary).toList();
    }

    private <T> String resolveNextCursor(List<T> items, int size) {
        if (items.isEmpty() || items.size() < size) {
            return null;
        }
        Object last = items.getLast();
        if (last instanceof PostSummary summary) {
            return CursorPagingHelper.encodeCursor(summary.createdAt(), summary.id());
        }
        if (last instanceof Member member) {
            return CursorPagingHelper.encodeCursor(member.getCreatedAt(), member.getId());
        }
        return null;
    }

    private PostSummary toSummary(PostSummaryProjection row) {
        return PostSummary.of(row.getId(), row.getImageObjectKey(), row.getCreatedAt());
    }
}
