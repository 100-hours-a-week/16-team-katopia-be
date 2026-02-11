package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.StyleType;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import lombok.Builder;

import java.util.Set;

@Builder
public record MemberProfile(
        @Schema(description = Policy.NICKNAME_DES, example = Docs.NICKNAME)
        String nickname,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String profileImageObjectKey,
        @Schema(description = Policy.GENDER_DES, example = Docs.GENDER)
        String gender,
        @Schema(description = Policy.HEIGHT_DES, example = Docs.HEIGHT)
        Short height,
        @Schema(description = Policy.WEIGHT_DES, example = Docs.WEIGHT)
        Short weight,
        @ArraySchema(
                arraySchema = @Schema(description = Docs.STYLE_LIST_DES, example = Docs.STYLE_LIST)
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
