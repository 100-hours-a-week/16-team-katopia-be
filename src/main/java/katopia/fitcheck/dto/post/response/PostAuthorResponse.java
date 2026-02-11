package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

@Builder
public record PostAuthorResponse(
        @Schema(description = SwaggerExamples.MEMBER_ID_DES, example = SwaggerExamples.MEMBER_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.NICKNAME_DES, example = SwaggerExamples.NICKNAME)
        String nickname,
        @Schema(description = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY)
        String profileImageObjectKey,
        @Schema(description = SwaggerExamples.GENDER_DES, example = SwaggerExamples.GENDER_M)
        String gender,
        @Schema(description = SwaggerExamples.HEIGHT_DES, example = SwaggerExamples.HEIGHT_175)
        Short height,
        @Schema(description = SwaggerExamples.WEIGHT_DES, example = SwaggerExamples.WEIGHT_70)
        Short weight
) {
    public static PostAuthorResponse of(Member author) {
        if (author.getAccountStatus() == AccountStatus.WITHDRAWN) {
            return PostAuthorResponse.builder()
                    .id(null)
                    .nickname(MemberDisplayConstants.WITHDRAWN_NICKNAME)
                    .profileImageObjectKey(null)
                    .gender(null)
                    .height(null)
                    .weight(null)
                    .build();
        }
        return PostAuthorResponse.builder()
                .id(author.getId())
                .nickname(author.getNickname())
                .profileImageObjectKey(author.getProfileImageObjectKey())
                .gender(author.getGender() != null ? author.getGender().name() : null)
                .height(author.getHeight())
                .weight(author.getWeight())
                .build();
    }
}
