package katopia.fitcheck.service.auth;

import katopia.fitcheck.domain.auth.RefreshToken;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenCommandService refreshTokenCommandService;
    private final RefreshTokenQueryService refreshTokenQueryService;

    @Transactional
    public RefreshToken issue(Long memberId, JwtProvider.Token refreshToken) {
        return refreshTokenCommandService.issue(memberId, refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenQueryService.findByTokenHash(tokenHash);
    }

    @Transactional
    public void revoke(RefreshToken refreshToken, LocalDateTime now) {
        refreshTokenCommandService.revoke(refreshToken, now);
    }

    @Transactional
    public void revokeAllByMemberId(Long memberId, LocalDateTime now) {
        refreshTokenCommandService.revokeAllByMemberId(memberId, now);
    }

    public void cleanupExpiredTokens() {
        refreshTokenCommandService.cleanupExpiredTokens();
    }

}
