package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.docs.Docs;

@Schema(description = "참여 가능한 투표 항목")
public record VoteCandidateItemResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String imageObjectKey,
        @Schema(description = Docs.ORDER_DES, example = "1")
        int sortOrder
) {
    public static VoteCandidateItemResponse of(VoteItem item) {
        return new VoteCandidateItemResponse(item.getId(), item.getImageObjectKey(), item.getSortOrder());
    }
}
