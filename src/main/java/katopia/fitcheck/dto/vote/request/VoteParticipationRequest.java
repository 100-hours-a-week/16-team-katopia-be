package katopia.fitcheck.dto.vote.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.validation.VoteItemIds;

import java.util.List;

public record VoteParticipationRequest(
        @ArraySchema(
                arraySchema = @Schema(description = Docs.VOTE_ITEM_ID_LIST_DES, example = Docs.VOTE_ITEM_ID_LIST_EXAMPLE),
                schema = @Schema(description = Docs.VOTE_ITEM_ID_DES, example = "1"),
                minItems = Policy.VOTE_IMAGE_MIN_COUNT,
                maxItems = Policy.VOTE_IMAGE_MAX_COUNT
        )
        @VoteItemIds
        List<Long> voteItemIds
) {
}
