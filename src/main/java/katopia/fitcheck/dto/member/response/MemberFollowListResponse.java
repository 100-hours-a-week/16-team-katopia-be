package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberFollowListResponse(
        @Schema(description = "팔로우 목록")
        List<MemberFollowSummary> members,
        @Schema(description = SwaggerExamples.FOLLOW_CURSOR_DES, example = SwaggerExamples.FOLLOW_CURSOR_EXAMPLE)
        String nextCursor
) {
    public static MemberFollowListResponse of(List<MemberFollowSummary> members, String nextCursor) {
        return MemberFollowListResponse.builder()
                .members(members)
                .nextCursor(nextCursor)
                .build();
    }
}
