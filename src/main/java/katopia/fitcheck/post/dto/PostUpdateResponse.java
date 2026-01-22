package katopia.fitcheck.post.dto;

import katopia.fitcheck.post.domain.Post;
import katopia.fitcheck.post.domain.PostImage;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostUpdateResponse(
        String content,
        List<PostImage> imageUrls,
        LocalDateTime updatedAt
) {
    public static PostUpdateResponse of(Post post) {
        return PostUpdateResponse.builder()
                .content(post.getContent())
                .imageUrls(post.getImageUrls())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
