package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

@Schema(description = "투표 요약")
public record VoteSummary(
        @Schema(description = "투표 ID", example = SwaggerExamples.VOTE_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.VOTE_TITLE_DES, example = SwaggerExamples.VOTE_TITLE)
        String title,
        @Schema(description = "종료 여부", example = "false")
        boolean isClosed
) {
    public static VoteSummary of(Long id, String title, boolean isClosed) {
        return new VoteSummary(id, title, isClosed);
    }
}
