package katopia.fitcheck.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResponse(
        @Schema(description = "추천 회원 목록")
        List<RecommendationMemberResponse> members
) {
    public static RecommendationResponse of(List<RecommendationMemberResponse> members) {
        return RecommendationResponse.builder()
                .members(members)
                .build();
    }
}
