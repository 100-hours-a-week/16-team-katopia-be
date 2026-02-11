package katopia.fitcheck.dto.comment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.validation.CommentContent;

public record CommentRequest(
        @Schema(
                description = Docs.COMMENT_CONTENT_DES,
                example = Docs.COMMENT_CONTENT,
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = Policy.COMMENT_CONTENT_MIN_LENGTH,
                maxLength = Policy.COMMENT_CONTENT_MAX_LENGTH
        )
        @CommentContent
        String content
) {
}
