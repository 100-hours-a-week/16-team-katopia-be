package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import lombok.Builder;

@Builder
public record PostAuthorResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Policy.NICKNAME_DES, example = Docs.NICKNAME)
        String nickname,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String profileImageObjectKey,
        @Schema(description = Policy.GENDER_DES, example = Docs.GENDER)
        String gender,
        @Schema(description = Policy.HEIGHT_DES, example = Docs.HEIGHT)
        Short height,
        @Schema(description = Policy.WEIGHT_DES, example = Docs.WEIGHT)
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
