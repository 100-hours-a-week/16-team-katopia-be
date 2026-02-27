package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberFollowListResponse(
        @Schema(description = Docs.FOLLOW_LIST_DES)
        List<MemberFollowSummary> members,
        @Schema(description = Docs.CURSOR_DES, example = Docs.CURSOR)
        String nextCursor
) {
    public static MemberFollowListResponse of(List<MemberFollowSummary> members, String nextCursor) {
        return MemberFollowListResponse.builder()
                .members(members)
                .nextCursor(nextCursor)
                .build();
    }
}
