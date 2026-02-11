package katopia.fitcheck.dto.member.response;

import katopia.fitcheck.domain.member.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileDetailResponse(
    Long id,
    String nickname,
    String profileImageObjectKey,
    boolean enableRealtimeNotification,
    String email,
    MemberProfile profile,
    MemberAggregate aggregate,
    LocalDateTime updatedAt
) {
    public static MemberProfileDetailResponse of(Member member) {
        return MemberProfileDetailResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageObjectKey(member.getProfileImageObjectKey())
                .enableRealtimeNotification(member.isEnableRealtimeNotification())
                .email(member.getEmail())
                .profile(MemberProfile.of(member))
                .aggregate(member.getAggregate())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
