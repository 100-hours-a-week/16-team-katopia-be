package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.domain.comment.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        @Schema(description = "댓글 ID", example = "1")
        Long id,
        @Schema(description = "댓글 본문", example = "댓글입니다")
        String content,
        @Schema(description = "작성 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
        LocalDateTime createdAt,
        @Schema(description = "수정 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
        LocalDateTime updatedAt
) {
    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
