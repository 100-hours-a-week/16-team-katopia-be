package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostSummary(
        @Schema(description = "게시글 ID", example = "1")
        Long id,
        @Schema(description = "대표 이미지 오브젝트 키", example = "posts/1/1700000000000-uuid.png")
        String imageObjectKey,
        @Schema(description = "작성 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
        LocalDateTime createdAt
) {
    public static PostSummary of(Long id, String imageObjectKey, LocalDateTime createdAt) {
        return PostSummary.builder()
                .id(id)
                .imageObjectKey(imageObjectKey)
                .createdAt(createdAt)
                .build();
    }
}
