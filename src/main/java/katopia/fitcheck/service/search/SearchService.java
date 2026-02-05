package katopia.fitcheck.service.search;

import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.dto.post.response.PostSummary;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.dto.search.PostSearchResponse;
import katopia.fitcheck.dto.search.MemberSearchResponse;
import katopia.fitcheck.dto.search.MemberSummary;
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
    private final PostRepository postRepository;
    private final SearchValidator searchValidator;
    private static final int MAX_FULLTEXT_QUERY_LENGTH = 200;

    @Transactional(readOnly = true)
    @katopia.fitcheck.global.aop.SearchLog("users")
    public MemberSearchResponse searchUsers(
                                          String query,
                                          String sizeValue,
                                          String after) {
        String keyword = LikeEscapeHelper.escape(searchValidator.requireQuery(query));
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Member> members = loadUsers(keyword, size, after);
        List<MemberSummary> summaries = members.stream()
                .map(MemberSummary::of)
                .toList();
        String nextCursor = resolveNextCursor(members, size);
        return MemberSearchResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    @katopia.fitcheck.global.aop.SearchLog("posts")
    public PostSearchResponse searchPosts(String query,
                                          String sizeValue,
                                          String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = loadPosts(query, size, after);
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.builder()
                        .id(post.getId())
                        .imageObjectKey(post.getImageObjectKeys().getFirst().getImageObjectKey())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();
        String nextCursor = resolveNextCursor(posts, size);
        return PostSearchResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    @katopia.fitcheck.global.aop.SearchLog("posts-fulltext")
    public PostSearchResponse searchPostsFulltext(String query, String sizeValue) {
        String keyword = searchValidator.requireQuery(query, MAX_FULLTEXT_QUERY_LENGTH);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = postRepository.searchLatestByContentFulltext(
                keyword,
                AccountStatus.ACTIVE.name(),
                size
        );
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.builder()
                        .id(post.getId())
                        .imageObjectKey(post.getImageObjectKeys().getFirst().getImageObjectKey())
                        .createdAt(post.getCreatedAt())
                        .build())
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

    private List<Post> loadPosts(String query, int size, String after) {
        boolean tagOnly = query.startsWith("#");
        String keyword = tagOnly ? query.substring(1).trim() : query;
        keyword = LikeEscapeHelper.escape(searchValidator.requireQuery(keyword));
        PageRequest pageRequest = PageRequest.of(0, size);
        if (!StringUtils.hasText(after)) {
            return tagOnly
                    ? postRepository.searchLatestByTag(keyword, AccountStatus.ACTIVE, pageRequest)
                    : postRepository.searchLatestByContent(keyword, AccountStatus.ACTIVE, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return tagOnly
                ? postRepository.searchPageAfterByTag(keyword, AccountStatus.ACTIVE, cursor.createdAt(), cursor.id(), pageRequest)
                : postRepository.searchPageAfterByContent(keyword, AccountStatus.ACTIVE, cursor.createdAt(), cursor.id(), pageRequest);
    }

    private <T> String resolveNextCursor(List<T> items, int size) {
        if (items.isEmpty() || items.size() < size) {
            return null;
        }
        Object last = items.getLast();
        if (last instanceof Member member) {
            return CursorPagingHelper.encodeCursor(member.getCreatedAt(), member.getId());
        }
        if (last instanceof Post post) {
            return CursorPagingHelper.encodeCursor(post.getCreatedAt(), post.getId());
        }
        return null;
    }
}
