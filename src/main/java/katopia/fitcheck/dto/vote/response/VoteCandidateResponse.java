package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "참여 가능한 최신 투표 응답")
public record VoteCandidateResponse(
        @Schema(description = "투표 ID", example = SwaggerExamples.VOTE_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.VOTE_TITLE_DES, example = SwaggerExamples.VOTE_TITLE)
        String title,
        @Schema(description = "투표 항목 목록")
        List<VoteCandidateItemResponse> items
) {
    public static VoteCandidateResponse of(Vote vote, List<VoteItem> items) {
        return VoteCandidateResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .items(items.stream().map(VoteCandidateItemResponse::of).toList())
                .build();
    }
}
