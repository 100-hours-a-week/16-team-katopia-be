package katopia.fitcheck.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.StyleType;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record RecommendationMemberResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.NICKNAME_DES, example = Docs.NICKNAME)
        String nickname,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String profileImageObjectKey,
        @Schema(description = Docs.HEIGHT_DES, example = Docs.HEIGHT)
        Short height,
        @Schema(description = Docs.WEIGHT_DES, example = Docs.WEIGHT)
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
