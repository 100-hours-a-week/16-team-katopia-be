package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import lombok.Builder;

@Builder
public record PostAuthorResponse(
        Long id,
        String nickname,
        String profileImageObjectKey,
        String gender,
        Short height,
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
