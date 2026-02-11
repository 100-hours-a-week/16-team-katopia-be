package katopia.fitcheck.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.StyleType;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record RecommendationMemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,
        @Schema(description = "닉네임", example = "dev_user")
        String nickname,
        @Schema(description = "프로필 이미지 objectKey", example = "profile/1/1-uuid.png")
        String profileImageObjectKey,
        @Schema(description = "키(cm)", example = "175")
        Short height,
        @Schema(description = "몸무게(kg)", example = "70")
        Short weight,
        @Schema(description = "선호 스타일 목록", example = "[\"MINIMAL\",\"CASUAL\"]")
        List<String> styles
) {
    public static RecommendationMemberResponse of(Member member) {
        return RecommendationMemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageObjectKey(member.getProfileImageObjectKey())
                .height(member.getHeight())
                .weight(member.getWeight())
                .styles(toStyleNames(member.getStyles()))
                .build();
    }

    private static List<String> toStyleNames(Set<StyleType> styles) {
        if (styles == null || styles.isEmpty()) {
            return List.of();
        }
        return styles.stream().map(Enum::name).toList();
    }
}
