package katopia.fitcheck.dto.member.response;

import katopia.fitcheck.domain.member.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileResponse(
    Long id,
    MemberProfile profile,
    MemberAggregate aggregate,
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
