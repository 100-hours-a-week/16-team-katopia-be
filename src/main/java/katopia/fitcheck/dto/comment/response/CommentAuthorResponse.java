package katopia.fitcheck.dto.comment.response;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import lombok.Builder;

@Builder
public record CommentAuthorResponse(
        Long id,
        String nickname,
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
