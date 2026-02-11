package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.domain.comment.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentSummary(
        @Schema(description = "댓글 ID", example = "1")
        Long id,
        @Schema(description = "작성자 정보")
        CommentAuthorResponse author,
        @Schema(description = "댓글 본문", example = "댓글입니다")
        String content,
        @Schema(description = "작성 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
        LocalDateTime createdAt
) {
    public static CommentSummary of(Comment comment) {
        return CommentSummary.builder()
                .id(comment.getId())
                .author(CommentAuthorResponse.of(comment.getMember()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
