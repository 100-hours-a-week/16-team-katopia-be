package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.docs.SwaggerExamples;

import java.math.BigDecimal;

@Schema(description = "투표 결과 항목")
public record VoteResultItemResponse(
        @Schema(description = SwaggerExamples.VOTE_ITEM_ID_DES, example = SwaggerExamples.VOTE_ITEM_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.VOTE_IMAGE_OBJECT_KEY_EXAMPLE)
        String imageObjectKey,
        @Schema(description = "항목 순서", example = "1")
        int sortOrder,
        @Schema(description = "득표 수", example = "12")
        long fitCount,
        @Schema(description = "득표율(%)", example = "33.33")
        BigDecimal fitRate
) {
    public static VoteResultItemResponse of(VoteItem item, BigDecimal fitRate) {
        return new VoteResultItemResponse(
                item.getId(),
                item.getImageObjectKey(),
                item.getSortOrder(),
                item.getFitCount(),
                fitRate
        );
    }
}
