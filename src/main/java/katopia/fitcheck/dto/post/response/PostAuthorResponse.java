package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.AccountStatus;
import lombok.Builder;

@Builder
public record PostAuthorResponse(
        String nickname,
        String profileImageUrl,
        String gender,
        Short height,
        Short weight
) {
    public static PostAuthorResponse of (Member author) {
        if (author.getAccountStatus() == AccountStatus.WITHDRAWN) {
            return PostAuthorResponse.builder()
                    .nickname("알 수 없음")
                    .profileImageUrl(null)
                    .gender(null)
                    .height(null)
                    .weight(null)
                    .build();
        }
        return PostAuthorResponse.builder()
                .nickname(author.getNickname())
                .profileImageUrl(author.getProfileImageUrl())
                .gender(author.getGender() != null ? author.getGender().name() : null)
                .height(author.getHeight())
                .weight(author.getWeight())
                .build();
    }
}
