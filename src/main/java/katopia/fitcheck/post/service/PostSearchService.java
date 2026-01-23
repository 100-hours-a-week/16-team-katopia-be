package katopia.fitcheck.post.service;

import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.member.domain.Member;
import katopia.fitcheck.post.domain.Post;
import katopia.fitcheck.post.dto.PostDetailResponse;
import katopia.fitcheck.post.dto.PostListResponse;
import katopia.fitcheck.post.dto.PostSummary;
import katopia.fitcheck.post.repository.PostRepository;
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

    @Transactional(readOnly = true)
    public PostListResponse list(String sizeValue, String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Post> posts = loadPosts(size, after);
        List<PostSummary> summaries = posts.stream()
                .map(post -> PostSummary.builder()
                        .id(post.getId())
                        .imageUrls(post.getImageUrls().getFirst().getImageUrl())
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
    public PostDetailResponse getDetail(Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);
        Member author = post.getMember();

        return PostDetailResponse.of(post, author);
    }

    private List<Post> loadPosts(int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return postRepository.findLatest(pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return postRepository.findPageAfter(cursor.createdAt(), cursor.id(), pageRequest);
    }
}
