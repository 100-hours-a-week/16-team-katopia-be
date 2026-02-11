package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record VoteListResponse(
        @Schema(description = "투표 목록")
        List<VoteSummary> votes,
        @io.swagger.v3.oas.annotations.media.Schema(
                description = katopia.fitcheck.global.docs.SwaggerExamples.VOTE_CURSOR_DES,
                example = katopia.fitcheck.global.docs.SwaggerExamples.VOTE_CURSOR_EXAMPLE
        )
        String nextCursor
) {
    public static VoteListResponse of(List<VoteSummary> votes, String nextCursor) {
        return VoteListResponse.builder()
                .votes(votes)
                .nextCursor(nextCursor)
                .build();
    }
}
