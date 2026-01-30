package katopia.fitcheck.dto.post;

import katopia.fitcheck.domain.post.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostUpdateResponse(
        String content,
        LocalDateTime updatedAt
) {
    public static PostUpdateResponse of(Post post) {
        return PostUpdateResponse.builder()
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
