package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.domain.comment.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentSummary(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.AUTHOR_DES)
        CommentAuthorResponse author,
        @Schema(description = Docs.COMMENT_CONTENT_DES, example = Docs.COMMENT_CONTENT)
        String content,
        @Schema(description = Docs.CREATED_AT_DES, example = Docs.TIMESTAMP)
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
