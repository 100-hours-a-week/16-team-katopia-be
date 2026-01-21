package katopia.fitcheck.auth;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenPair;
import katopia.fitcheck.repository.MemberRepository;
import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public TokenRefreshResult refreshTokens(String refreshToken) {
        Long memberId = jwtProvider.extractMemberId(refreshToken, JwtProvider.TokenType.REFRESH);

        if (memberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_RT);
        }


        Member member = memberRepository.findById(memberId)
                .filter(m -> m.getAccountStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_RT));

        TokenPair pair = jwtProvider.issueTokens(member.getId());
        return new TokenRefreshResult(pair.accessToken(), pair.refreshToken());
    }

    public record TokenRefreshResult(
            JwtProvider.Token accessToken,
            JwtProvider.Token refreshToken
    ) { }
}
