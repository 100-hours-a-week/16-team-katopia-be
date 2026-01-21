package katopia.fitcheck.global.security.jwt;

import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Member;
import lombok.Builder;


@Builder
public record LoginResponse(
        AccountStatus status,
        String nickname,
        String email,
        String accessToken
) {

    public static LoginResponse of(Member member, String accessToken) {
        return LoginResponse.builder()
                .status(member.getAccountStatus())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .accessToken(accessToken)
                .build();
    }
}
