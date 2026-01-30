package katopia.fitcheck.dto.comment.response;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import lombok.Builder;

@Builder
public record CommentAuthorResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {
    public static CommentAuthorResponse of(Member member) {
        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            return CommentAuthorResponse.builder()
                    .id(member.getId())
                    .nickname("알 수 없음")
                    .profileImageUrl(null)
                    .build();
        }
        return CommentAuthorResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
