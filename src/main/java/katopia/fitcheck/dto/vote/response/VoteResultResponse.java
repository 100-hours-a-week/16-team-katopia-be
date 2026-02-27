package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Builder
@Schema(description = "투표 결과 응답")
public record VoteResultResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.VOTE_TITLE_DES, example = Docs.VOTE_TITLE)
        String title,
        @Schema(description = "투표 결과 항목")
        List<VoteResultItemResponse> items
) {
    public static VoteResultResponse of(Vote vote, List<VoteItem> items) {
        long totalCount = items.stream()
                .mapToLong(VoteItem::getFitCount)
                .sum();
        List<VoteResultItemResponse> responses = items.stream()
                .map(item -> VoteResultItemResponse.of(item, calculateRate(item.getFitCount(), totalCount)))
                .toList();
        return VoteResultResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .items(responses)
                .build();
    }

    private static BigDecimal calculateRate(long itemCount, long totalCount) {
        if (totalCount == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(itemCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
    }
}
