package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

@Builder
public record CommentAuthorResponse(
        @Schema(description = SwaggerExamples.MEMBER_ID_DES, example = SwaggerExamples.MEMBER_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.NICKNAME_DES, example = SwaggerExamples.NICKNAME)
        String nickname,
        @Schema(description = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY)
        String profileImageObjectKey
) {
    public static CommentAuthorResponse of(Member member) {
        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            return CommentAuthorResponse.builder()
                    .id(member.getId())
                    .nickname(MemberDisplayConstants.WITHDRAWN_NICKNAME)
                    .profileImageObjectKey(null)
                    .build();
        }
        return CommentAuthorResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageObjectKey(member.getProfileImageObjectKey())
                .build();
    }
}
