package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.docs.SwaggerExamples;

@Schema(description = "참여 가능한 투표 항목")
public record VoteCandidateItemResponse(
        @Schema(description = SwaggerExamples.VOTE_ITEM_ID_DES, example = SwaggerExamples.VOTE_ITEM_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.VOTE_IMAGE_OBJECT_KEY_EXAMPLE)
        String imageObjectKey,
        @Schema(description = "항목 순서", example = "1")
        int sortOrder
) {
    public static VoteCandidateItemResponse of(VoteItem item) {
        return new VoteCandidateItemResponse(item.getId(), item.getImageObjectKey(), item.getSortOrder());
    }
}
