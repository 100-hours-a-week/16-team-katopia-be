package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

@Builder
public record CommentAuthorResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.NICKNAME_DES, example = Docs.NICKNAME)
        String nickname,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
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
