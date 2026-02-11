package katopia.fitcheck.dto.vote.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.ImageObjectKeys;
import katopia.fitcheck.global.validation.VoteTitle;

import java.util.List;

public record VoteCreateRequest(
        @Schema(
                description = SwaggerExamples.VOTE_TITLE_DES,
                example = SwaggerExamples.VOTE_TITLE,
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 20
        )
        @VoteTitle
        String title,

        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.IMAGE_OBJECT_KEY_LIST_DES, example = SwaggerExamples.VOTE_IMAGE_OBJECT_KEY_LIST),
                schema = @Schema(description = SwaggerExamples.IMAGE_OBJECT_KEY_DES),
                minItems = 1,
                maxItems = 5
        )
        @ImageObjectKeys(category = "VOTE")
        List<String> imageObjectKeys
) {
}
