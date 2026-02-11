package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileResponse(
    @Schema(description = Docs.ID_DES, example = "1")
    Long id,
    @Schema(description = Docs.PROFILE_DES)
    MemberProfile profile,
    @Schema(description = Docs.AGGREGATE_DES)
    MemberAggregate aggregate,
    @Schema(description = Docs.UPDATED_AT_DES, example = Docs.TIMESTAMP)
    LocalDateTime updatedAt
) {
    public static MemberProfileResponse of(Member member) {
        return MemberProfileResponse.builder()
                .id(member.getId())
                .profile(MemberProfile.of(member))
                .aggregate(member.getAggregate())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
