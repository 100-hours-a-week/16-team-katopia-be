package katopia.fitcheck.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

public record CommentCreateRequest(
        @Schema(description = SwaggerExamples.COMMENT_CONTENT_DES, example = SwaggerExamples.COMMENT_CONTENT_CREATE)
        String content
) {
}
