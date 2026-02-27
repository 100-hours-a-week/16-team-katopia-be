package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record VoteListResponse(
        @Schema(description = "투표 목록")
        List<VoteSummary> votes,
        @Schema(description = Docs.CURSOR_DES, example = Docs.CURSOR)
        String nextCursor
) {
    public static VoteListResponse of(List<VoteSummary> votes, String nextCursor) {
        return VoteListResponse.builder()
                .votes(votes)
                .nextCursor(nextCursor)
                .build();
    }
}
