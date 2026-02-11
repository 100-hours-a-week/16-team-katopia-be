package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostUpdateResponse(
        @Schema(description = "본문", example = "수정된 본문")
        String content,
        @Schema(description = "수정 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
        LocalDateTime updatedAt
) {
    public static PostUpdateResponse of(Post post) {
        return PostUpdateResponse.builder()
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
