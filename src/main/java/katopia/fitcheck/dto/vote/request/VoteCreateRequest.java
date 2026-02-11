package katopia.fitcheck.dto.vote.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.validation.ImageObjectKeys;
import katopia.fitcheck.global.validation.VoteTitle;

import java.util.List;

public record VoteCreateRequest(
        @Schema(
                description = Docs.VOTE_TITLE_DES,
                example = Docs.VOTE_TITLE,
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = Policy.VOTE_TITLE_MIN_LENGTH,
                maxLength = Policy.VOTE_TITLE_MAX_LENGTH
        )
        @VoteTitle
        String title,

        @ArraySchema(
                arraySchema = @Schema(description = Docs.IMAGE_OBJECT_KEY_LIST_DES, example = Docs.VOTE_IMAGE_OBJECT_KEY_LIST),
                schema = @Schema(description = Docs.IMAGE_OBJECT_KEY_DES),
                minItems = Policy.VOTE_IMAGE_MIN_COUNT,
                maxItems = Policy.VOTE_IMAGE_MAX_COUNT
        )
        @ImageObjectKeys(category = "VOTE")
        List<String> imageObjectKeys
) {
}
