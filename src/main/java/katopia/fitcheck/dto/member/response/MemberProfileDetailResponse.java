package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileDetailResponse(
    @Schema(description = Docs.ID_DES, example = "1")
    Long id,
    @Schema(description = Policy.NICKNAME_DES, example = Docs.NICKNAME)
    String nickname,
    @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
    String profileImageObjectKey,
    @Schema(description = Docs.NOTIFICATION_DES, example = Docs.NOTIFICATION)
    boolean enableRealtimeNotification,
    @Schema(description = Docs.EMAIL_DES, example = Docs.EMAIL)
    String email,
    @Schema(description = Docs.PROFILE_DES)
    MemberProfile profile,
    @Schema(description = Docs.AGGREGATE_DES)
    MemberAggregate aggregate,
    @Schema(description = Docs.UPDATED_AT_DES, example = Docs.TIMESTAMP)
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
