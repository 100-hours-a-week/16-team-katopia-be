package katopia.fitcheck.dto.vote.response;

import lombok.Builder;

import java.util.List;

@Builder
public record VoteListResponse(
        List<VoteSummary> votes,
        String nextCursor
) {
    public static VoteListResponse of(List<VoteSummary> votes, String nextCursor) {
        return VoteListResponse.builder()
                .votes(votes)
                .nextCursor(nextCursor)
                .build();
    }
}
