package katopia.fitcheck.service.post;

import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.dto.post.response.PostDetailResponse;
import katopia.fitcheck.dto.post.response.PostListResponse;
import katopia.fitcheck.dto.post.response.PostSummary;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.repository.post.PostTagRepository;
import katopia.fitcheck.repository.post.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostSearchService {

    private final PostRepository postRepository;
    private final PostFinder postFinder;
    private final MemberFinder memberFinder;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional(readOnly = true)
    public PostListResponse list(String sizeValue, String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = loadPosts(size, after);
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.builder()
                        .id(post.getId())
                        .imageObjectKey(post.getImageObjectKeys().getFirst().getImageObjectKey())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();

        String nextCursor = null;
        if (!posts.isEmpty() && posts.size() == size) {
            Post last = posts.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.getCreatedAt(), last.getId());
        }

        return PostListResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    public PostListResponse listByMember(Long memberId, String sizeValue, String after) {
        memberFinder.findActiveByIdOrThrow(memberId);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = loadPostsByMember(memberId, size, after);
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.builder()
                        .id(post.getId())
                        .imageObjectKey(post.getImageObjectKeys().getFirst().getImageObjectKey())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();

        String nextCursor = null;
        if (!posts.isEmpty() && posts.size() == size) {
            Post last = posts.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.getCreatedAt(), last.getId());
        }

        return PostListResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getDetail(Long memberId, Long postId) {
        Post post = postFinder.findDetailByIdOrThrow(postId);
        Member author = post.getMember();

        List<String> tags = postTagRepository.findTagNamesByPostId(postId);
        boolean isLiked = memberId != null && postLikeRepository.existsByMemberIdAndPostId(memberId, postId);
        return PostDetailResponse.of(post, author, tags, isLiked);
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
}
