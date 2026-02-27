package katopia.fitcheck.service.auth;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenPair;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenRefreshResult refreshTokens(String refreshToken) {
        LocalDateTime now = LocalDateTime.now();
        Long memberId = jwtProvider.extractMemberId(refreshToken, JwtProvider.TokenType.REFRESH);

        if (memberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_RT);
        }

        String tokenHash = RefreshTokenHashSupport.hash(refreshToken);
        var tokenEntity = refreshTokenService.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_RT));

        if (tokenEntity.isRevoked() || tokenEntity.isExpired(now)) {
            refreshTokenService.revokeAllByMemberId(memberId, now);
            throw new AuthException(AuthErrorCode.INVALID_RT);
        }

        if (memberRepository.existsByIdAndAccountStatus(memberId, AccountStatus.WITHDRAWN)) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER);
        }

        refreshTokenService.revoke(tokenEntity, now);
        TokenPair pair = jwtProvider.issueTokens(memberId);
        refreshTokenService.issue(memberId, pair.refreshToken());

        return new TokenRefreshResult(pair.accessToken().token(), jwtProvider.buildRefreshCookie(pair.refreshToken()));
    }

    @Transactional
    public void revokeByRefreshToken(String refreshToken) {
        Long memberId = jwtProvider.extractMemberId(refreshToken, JwtProvider.TokenType.REFRESH);
        if (memberId == null) {
            return;
        }
        refreshTokenService.revokeAllByMemberId(memberId, LocalDateTime.now());
    }

    public record TokenRefreshResult(
            String accessToken,
            ResponseCookie refreshToken
    ) { }
}
