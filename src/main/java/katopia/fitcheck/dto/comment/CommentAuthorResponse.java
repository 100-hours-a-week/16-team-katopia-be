package katopia.fitcheck.dto.comment;

import katopia.fitcheck.domain.member.Member;
import lombok.Builder;

@Builder
public record CommentAuthorResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {
    public static CommentAuthorResponse of(Member member) {
        return CommentAuthorResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
