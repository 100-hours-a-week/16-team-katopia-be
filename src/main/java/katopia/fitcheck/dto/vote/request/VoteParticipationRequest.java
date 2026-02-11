package katopia.fitcheck.dto.vote.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.VoteItemIds;

import java.util.List;

public record VoteParticipationRequest(
        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.VOTE_ITEM_ID_LIST_DES, example = SwaggerExamples.VOTE_ITEM_ID_LIST_EXAMPLE),
                schema = @Schema(description = SwaggerExamples.VOTE_ITEM_ID_DES, example = SwaggerExamples.VOTE_ITEM_ID_EXAMPLE),
                minItems = 1,
                maxItems = 5
        )
        @VoteItemIds
        List<Long> voteItemIds
) {
}
