package katopia.fitcheck.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

public record CommentUpdateRequest(
        @Schema(description = "댓글 본문", example = SwaggerExamples.COMMENT_CONTENT_UPDATE)
        String content
) {
}
