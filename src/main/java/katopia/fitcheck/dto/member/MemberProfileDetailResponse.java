package katopia.fitcheck.dto.member;

import katopia.fitcheck.domain.member.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileDetailResponse(
    Long id,
    boolean enableRealtimeNotification,
    String email,
    MemberProfile profile,
    LocalDateTime updatedAt
) {
    public static MemberProfileDetailResponse of(Member member) {
        return MemberProfileDetailResponse.builder()
                .id(member.getId())
                .enableRealtimeNotification(member.isEnableRealtimeNotification())
                .email(member.getEmail())
                .profile(MemberProfile.of(member))
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
