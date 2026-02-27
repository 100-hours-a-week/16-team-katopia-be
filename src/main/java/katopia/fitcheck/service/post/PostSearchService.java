package katopia.fitcheck.service.post;

import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.dto.post.response.PostDetailResponse;
import katopia.fitcheck.dto.post.response.PostResponse;
import katopia.fitcheck.dto.post.response.PostListResponse;
import katopia.fitcheck.dto.post.response.PostSummary;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.repository.post.PostBookmarkRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.repository.post.PostTagNameProjection;
import katopia.fitcheck.repository.post.PostTagRepository;
import katopia.fitcheck.repository.post.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostSearchService {

    private final PostRepository postRepository;
    private final PostFinder postFinder;
    private final MemberFinder memberFinder;
    private final MemberFollowRepository memberFollowRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional(readOnly = true)
    public PostListResponse list(String sizeValue, String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = loadPosts(size, after);
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.of(
                        post.getId(),
                        post.getImages().getFirst().getImageObjectKey(),
                        post.getCreatedAt()
                ))
                .toList();

        String nextCursor = CursorPagingHelper.resolveNextCursor(posts, size, Post::getCreatedAt, Post::getId);
        return PostListResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    public PostListResponse listByMember(Long memberId, String sizeValue, String after) {
        memberFinder.findActiveByIdOrThrow(memberId);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = loadPostsByMember(memberId, size, after);
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.of(
                        post.getId(),
                        post.getImages().getFirst().getImageObjectKey(),
                        post.getCreatedAt()
                ))
                .toList();

        String nextCursor = CursorPagingHelper.resolveNextCursor(posts, size, Post::getCreatedAt, Post::getId);
        return PostListResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getDetail(Long memberId, Long postId) {
        Post post = postFinder.findDetailByIdOrThrow(postId);
        Member author = post.getMember();

        List<String> tags = postTagRepository.findTagNamesByPostId(postId);
        boolean isLiked = memberId != null && postLikeRepository.existsByMemberIdAndPostId(memberId, postId);
        boolean isBookmarked = memberId != null && postBookmarkRepository.existsByMemberIdAndPostId(memberId, postId);
        return PostDetailResponse.of(post, author, tags, isLiked, isBookmarked);
    }

    @Transactional(readOnly = true)
    public PostResponse listHomeFeed(Long memberId, String sizeValue, String after) {
        memberFinder.findActiveByIdOrThrow(memberId);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Long> targetIds = resolveFeedTargetIds(memberId);
        if (targetIds.isEmpty()) {
            return PostResponse.of(List.of(), null);
        }

        List<Long> postIds = loadFeedPostIds(targetIds, size, after);
        if (postIds.isEmpty()) {
            return PostResponse.of(List.of(), null);
        }

        Map<Long, Post> postMap = loadFeedPosts(postIds);
        Map<Long, List<String>> tagsByPostId = loadTags(postMap.keySet());
        Set<Long> likedPostIds = postLikeRepository.findLikedPostIds(memberId, postIds);
        Set<Long> bookmarkedPostIds = postBookmarkRepository.findBookmarkedPostIds(memberId, postIds);

        List<PostDetailResponse> posts = new ArrayList<>();
        List<Post> orderedPosts = new ArrayList<>();
        for (Long postId : postIds) {
            Post post = postMap.get(postId);
            if (post == null) {
                continue;
            }
            List<String> tags = tagsByPostId.getOrDefault(postId, List.of());
            boolean isLiked = likedPostIds.contains(postId);
            boolean isBookmarked = bookmarkedPostIds.contains(postId);
            posts.add(PostDetailResponse.of(post, post.getMember(), tags, isLiked, isBookmarked));
            orderedPosts.add(post);
        }
        String nextCursor = CursorPagingHelper.resolveNextCursor(
                orderedPosts,
                size,
                Post::getCreatedAt,
                Post::getId
        );
        return PostResponse.of(posts, nextCursor);
    }

    private List<Post> loadPosts(int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return postRepository.findLatest(pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return postRepository.findPageAfter(cursor.createdAt(), cursor.id(), pageRequest);
    }

    private List<Post> loadPostsByMember(Long memberId, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return postRepository.findLatestByMemberId(memberId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return postRepository.findPageAfterByMemberId(memberId, cursor.createdAt(), cursor.id(), pageRequest);
    }

    private List<Long> resolveFeedTargetIds(Long memberId) {
        List<Long> followed = memberFollowRepository.findFollowedIdsByFollowerId(memberId);
        LinkedHashSet<Long> targets = new LinkedHashSet<>(followed);
        targets.add(memberId);
        return new ArrayList<>(targets);
    }

    private List<Long> loadFeedPostIds(List<Long> memberIds, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return postRepository.findFeedPostIdsLatest(memberIds, AccountStatus.ACTIVE, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return postRepository.findFeedPostIdsPageAfter(memberIds, AccountStatus.ACTIVE, cursor.createdAt(), cursor.id(), pageRequest);
    }

    private Map<Long, Post> loadFeedPosts(List<Long> postIds) {
        Set<Long> idSet = new LinkedHashSet<>(postIds);
        List<Post> posts = postRepository.findFeedDetailsByPostIds(idSet);
        Map<Long, Post> postMap = new LinkedHashMap<>();
        for (Post post : posts) {
            postMap.put(post.getId(), post);
        }
        return postMap;
    }

    private Map<Long, List<String>> loadTags(Set<Long> postIds) {
        Map<Long, List<String>> tags = new LinkedHashMap<>();
        if (postIds.isEmpty()) {
            return tags;
        }
        List<PostTagNameProjection> rows = postTagRepository.findTagNamesByPostIds(new ArrayList<>(postIds));
        for (PostTagNameProjection row : rows) {
            tags.computeIfAbsent(row.getPostId(), key -> new ArrayList<>()).add(row.getName());
        }
        return tags;
    }
}
