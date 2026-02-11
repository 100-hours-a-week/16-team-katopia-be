package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileDetailResponse(
    @Schema(description = SwaggerExamples.MEMBER_ID_DES, example = SwaggerExamples.MEMBER_ID_EXAMPLE)
    Long id,
    @Schema(description = SwaggerExamples.NICKNAME_DES, example = SwaggerExamples.NICKNAME)
    String nickname,
    @Schema(description = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY)
    String profileImageObjectKey,
    @Schema(description = SwaggerExamples.NOTIFICATION_DES, example = SwaggerExamples.NOTIFICATION_TRUE)
    boolean enableRealtimeNotification,
    @Schema(description = "이메일", example = "user@example.com")
    String email,
    @Schema(description = "프로필 정보")
    MemberProfile profile,
    @Schema(description = "집계 정보")
    MemberAggregate aggregate,
    @Schema(description = "수정 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
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
