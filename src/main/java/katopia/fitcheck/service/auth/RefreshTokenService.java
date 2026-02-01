package katopia.fitcheck.service.auth;

import katopia.fitcheck.domain.auth.RefreshToken;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.repository.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken issue(Long memberId, JwtProvider.Token refreshToken) {
        String tokenHash = hash(refreshToken.token());
        LocalDateTime expiresAt = LocalDateTime.ofInstant(refreshToken.expiresAt(), ZoneId.systemDefault());
        RefreshToken entity = RefreshToken.issue(memberId, tokenHash, expiresAt);
        return refreshTokenRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash);
    }

    @Transactional
    public void revoke(RefreshToken refreshToken, LocalDateTime now) {
        refreshToken.revoke(now);
    }

    @Transactional
    public void revokeAllByMemberId(Long memberId, LocalDateTime now) {
        refreshTokenRepository.revokeAllByMemberId(memberId, now);
    }

    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to hash refresh token", ex);
        }
    }

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredBefore(LocalDateTime.now());
    }
}
