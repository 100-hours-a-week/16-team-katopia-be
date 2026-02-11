package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.StyleType;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.util.Set;

@Builder
public record MemberProfile(
        @Schema(description = SwaggerExamples.NICKNAME_DES, example = SwaggerExamples.NICKNAME)
        String nickname,
        @Schema(description = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY)
        String profileImageObjectKey,
        @Schema(description = SwaggerExamples.GENDER_DES, example = SwaggerExamples.GENDER_M)
        String gender,
        @Schema(description = SwaggerExamples.HEIGHT_DES, example = SwaggerExamples.HEIGHT_175)
        Short height,
        @Schema(description = SwaggerExamples.WEIGHT_DES, example = SwaggerExamples.WEIGHT_70)
        Short weight,
        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.STYLE_LIST_DES, example = SwaggerExamples.STYLE_LIST)
        )
        Set<StyleType> style
) {
    public static MemberProfile of(Member member) {
        return MemberProfile.builder()
                .nickname(member.getNickname())
                .profileImageObjectKey(member.getProfileImageObjectKey())
                .gender(member.getGender() != null ? member.getGender().name() : null)
                .height(member.getHeight())
                .weight(member.getWeight())
                .style(member.getStyles() != null ? Set.copyOf(member.getStyles()) : null)
                .build();
    }
}
