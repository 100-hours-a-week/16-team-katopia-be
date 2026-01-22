package katopia.fitcheck.post.service;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.PostErrorCode;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostSearchService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final DateTimeFormatter CURSOR_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public PostListResponse list(String sizeValue, String after) {
        int size = resolvePageSize(sizeValue);
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
            nextCursor = encodeCursor(last.getCreatedAt(), last.getId());
        }

        return PostListResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
        Member author = post.getMember();

        return PostDetailResponse.of(post, author);
    }

    private int resolvePageSize(String sizeValue) {
        if (sizeValue == null) {
            return DEFAULT_PAGE_SIZE;
        }
        try {
            int parsed = Integer.parseInt(sizeValue);
            if (parsed <= 0) {
                throw new NumberFormatException("size must be positive");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new BusinessException(CommonErrorCode.INVALID_PAGE_SIZE_FORMAT);
        }
    }

    private List<Post> loadPosts(int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return postRepository.findLatest(pageRequest);
        }
        Cursor cursor = decodeCursor(after);
        return postRepository.findPageAfter(cursor.createdAt(), cursor.id(), pageRequest);
    }

    private Cursor decodeCursor(String cursorValue) {
        try {
            String[] parts = cursorValue.split("\\|");
            if (parts.length != 2) {
                throw new IllegalArgumentException("invalid cursor");
            }
            LocalDateTime createdAt = LocalDateTime.parse(parts[0], CURSOR_FORMATTER);
            Long id = Long.parseLong(parts[1]);
            return new Cursor(createdAt, id);
        } catch (Exception ex) {
            throw new BusinessException(CommonErrorCode.INVALID_ID_FORMAT);
        }
    }

    private String encodeCursor(LocalDateTime createdAt, Long id) {
        return String.format("%s|%d", CURSOR_FORMATTER.format(createdAt), id);
    }

    private record Cursor(LocalDateTime createdAt, Long id) { }
}
