package katopia.fitcheck.dto.comment.response;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
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
                    .nickname("알 수 없음")
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
