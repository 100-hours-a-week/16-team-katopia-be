package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.domain.comment.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.COMMENT_CONTENT_DES, example = Docs.COMMENT_CONTENT)
        String content,
        @Schema(description = Docs.CREATED_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime createdAt,
        @Schema(description = Docs.UPDATED_AT_DES, example = Docs.TIMESTAMP)
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
