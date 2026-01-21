package katopia.fitcheck.auth;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenPair;
import katopia.fitcheck.member.MemberRepository;
import katopia.fitcheck.member.domain.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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

        if (memberRepository.existsByIdAndAccountStatus(memberId, AccountStatus.WITHDRAWN)) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER);
        }

        TokenPair pair = jwtProvider.issueTokens(memberId);
        // TODO : RTR 관리 작업 필요 (테이블 또는 redis 관리 예정)

        return new TokenRefreshResult(pair.accessToken().token(), jwtProvider.buildRefreshCookie(pair.refreshToken()));
    }

    public record TokenRefreshResult(
            String accessToken,
            ResponseCookie refreshToken
    ) { }
}
