package katopia.fitcheck.search.service;

import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.member.repository.MemberRepository;
import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Gender;
import katopia.fitcheck.member.domain.Member;
import katopia.fitcheck.member.domain.MemberProfileValidator;
import katopia.fitcheck.post.domain.Post;
import katopia.fitcheck.post.dto.PostSummary;
import katopia.fitcheck.post.repository.PostRepository;
import katopia.fitcheck.search.dto.PostSearchResponse;
import katopia.fitcheck.search.dto.MemberSearchResponse;
import katopia.fitcheck.search.dto.MemberSummary;
import katopia.fitcheck.search.model.SearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final short BODY_RANGE_OFFSET = 3;

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberProfileValidator profileValidator;
    private final SearchValidator searchValidator;

    @Transactional(readOnly = true)
    public MemberSearchResponse searchUsers(Long requesterId,
                                          String query,
                                          String sizeValue,
                                          String after,
                                          String height,
                                          String weight,
                                          String gender) {
        String keyword = searchValidator.requireQuery(query);
        SearchFilter filter = resolveFilter(requesterId, height, weight, gender, false);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Member> members = loadUsers(keyword, filter, size, after, requesterId);
        List<MemberSummary> summaries = members.stream()
                .map(MemberSummary::of)
                .toList();
        String nextCursor = resolveNextCursor(members, size);
        return MemberSearchResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    public PostSearchResponse searchPosts(Long requesterId,
                                          String query,
                                          String sizeValue,
                                          String after,
                                          String height,
                                          String weight,
                                          String gender) {
        String keyword = searchValidator.requireQuery(query);
        SearchFilter filter = resolveFilter(requesterId, height, weight, gender, true);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = loadPosts(keyword, filter, size, after);
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.builder()
                        .id(post.getId())
                        .imageUrls(post.getImageUrls().getFirst().getImageUrl())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();
        String nextCursor = resolveNextCursor(posts, size);
        return PostSearchResponse.of(summaries, nextCursor);
    }

    private SearchFilter resolveFilter(Long requesterId, String height, String weight, String gender, boolean useRequesterProfile) {
        boolean hasExplicit = StringUtils.hasText(height) || StringUtils.hasText(weight) || StringUtils.hasText(gender);
        if (!useRequesterProfile || requesterId == null) {
            if (!hasExplicit) {
                return new SearchFilter(null, null, null, null, null);
            }
            Short parsedHeight = profileValidator.parseHeight(height);
            Short parsedWeight = profileValidator.parseWeight(weight);
            Gender parsedGender = profileValidator.parseGender(gender);
            return buildFilter(parsedHeight, parsedWeight, parsedGender);
        }
        return memberRepository.findByIdAndAccountStatus(requesterId, AccountStatus.ACTIVE)
                .map(member -> {
                    Short resolvedHeight = StringUtils.hasText(height) ? profileValidator.parseHeight(height) : member.getHeight();
                    Short resolvedWeight = StringUtils.hasText(weight) ? profileValidator.parseWeight(weight) : member.getWeight();
                    Gender resolvedGender = StringUtils.hasText(gender) ? profileValidator.parseGender(gender) : member.getGender();
                    return buildFilter(resolvedHeight, resolvedWeight, resolvedGender);
                })
                .orElseGet(() -> {
                    if (!hasExplicit) {
                        return new SearchFilter(null, null, null, null, null);
                    }
                    Short parsedHeight = profileValidator.parseHeight(height);
                    Short parsedWeight = profileValidator.parseWeight(weight);
                    Gender parsedGender = profileValidator.parseGender(gender);
                    return buildFilter(parsedHeight, parsedWeight, parsedGender);
                });
    }

    private SearchFilter buildFilter(Short height, Short weight, Gender gender) {
        Short minHeight = null;
        Short maxHeight = null;
        Short minWeight = null;
        Short maxWeight = null;
        if (height != null) {
            minHeight = (short) (height - BODY_RANGE_OFFSET);
            maxHeight = (short) (height + BODY_RANGE_OFFSET);
        }
        if (weight != null) {
            minWeight = (short) (weight - BODY_RANGE_OFFSET);
            maxWeight = (short) (weight + BODY_RANGE_OFFSET);
        }
        return new SearchFilter(minHeight, maxHeight, minWeight, maxWeight, gender);
    }

    private List<Member> loadUsers(String nickname, SearchFilter filter, int size, String after, Long excludeMemberId) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (!StringUtils.hasText(after)) {
            return memberRepository.searchLatestByNickname(
                    nickname,
                    AccountStatus.ACTIVE,
                    filter.minHeight(),
                    filter.maxHeight(),
                    filter.minWeight(),
                    filter.maxWeight(),
                    filter.gender(),
                    excludeMemberId,
                    pageRequest
            );
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return memberRepository.searchPageAfterByNickname(
                nickname,
                AccountStatus.ACTIVE,
                filter.minHeight(),
                filter.maxHeight(),
                filter.minWeight(),
                filter.maxWeight(),
                filter.gender(),
                excludeMemberId,
                cursor.createdAt(),
                cursor.id(),
                pageRequest
        );
    }

    private List<Post> loadPosts(String query, SearchFilter filter, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (!StringUtils.hasText(after)) {
            return postRepository.searchLatestByContentOrTag(
                    query,
                    AccountStatus.ACTIVE,
                    filter.minHeight(),
                    filter.maxHeight(),
                    filter.minWeight(),
                    filter.maxWeight(),
                    filter.gender(),
                    pageRequest
            );
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return postRepository.searchPageAfterByContentOrTag(
                query,
                AccountStatus.ACTIVE,
                filter.minHeight(),
                filter.maxHeight(),
                filter.minWeight(),
                filter.maxWeight(),
                filter.gender(),
                cursor.createdAt(),
                cursor.id(),
                pageRequest
        );
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
