package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostCreateResponse(
        Long id,
        String content,
        List<PostImage> imageUrls,
        LocalDateTime createdAt
) {
    public static PostCreateResponse of(Post post) {
        return PostCreateResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrls(post.getImages())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
