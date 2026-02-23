package katopia.fitcheck.service.auth;

import katopia.fitcheck.domain.auth.RefreshToken;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.repository.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class RefreshTokenCommandService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken issue(Long memberId, JwtProvider.Token refreshToken) {
        String tokenHash = RefreshTokenHashSupport.hash(refreshToken.token());
        LocalDateTime expiresAt = LocalDateTime.ofInstant(refreshToken.expiresAt(), ZoneId.systemDefault());
        RefreshToken entity = RefreshToken.issue(memberId, tokenHash, expiresAt);
        return refreshTokenRepository.save(entity);
    }

    @Transactional
    public void revoke(RefreshToken refreshToken, LocalDateTime now) {
        refreshToken.revoke(now);
    }

    @Transactional
    public void revokeAllByMemberId(Long memberId, LocalDateTime now) {
        refreshTokenRepository.revokeAllByMemberId(memberId, now);
    }

    @Scheduled(cron = Policy.REFRESH_TOKEN_CLEANUP_CRON)
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredBefore(LocalDateTime.now());
    }
}
