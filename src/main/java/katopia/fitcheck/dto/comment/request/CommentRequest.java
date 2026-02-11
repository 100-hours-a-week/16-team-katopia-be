package katopia.fitcheck.dto.comment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.CommentContent;

public record CommentRequest(
        @Schema(
                description = SwaggerExamples.COMMENT_CONTENT_DES,
                example = SwaggerExamples.COMMENT_CONTENT_CREATE,
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 200
        )
        @CommentContent
        String content
) {
}
