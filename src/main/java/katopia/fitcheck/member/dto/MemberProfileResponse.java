package katopia.fitcheck.member.dto;

import katopia.fitcheck.member.domain.Member;
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
                .aggregate(null) // TODO(v2): 프로필 조회 페이지 집계 섹션 추가
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
