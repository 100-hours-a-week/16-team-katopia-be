package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.global.docs.Docs;

import java.time.LocalDateTime;

@Schema(description = "투표 요약")
public record VoteSummary(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.VOTE_TITLE_DES, example = Docs.VOTE_TITLE)
        String title,
        @Schema(description = "종료 여부", example = "false")
        boolean isClosed
) {
    public static VoteSummary of(Vote vote, LocalDateTime now) {
        return new VoteSummary(vote.getId(), vote.getTitle(), !vote.getExpiresAt().isAfter(now));
    }
}
