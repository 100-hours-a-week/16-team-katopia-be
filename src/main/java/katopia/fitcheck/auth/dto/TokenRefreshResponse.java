package katopia.fitcheck.auth.dto;

import katopia.fitcheck.global.security.jwt.JwtProvider;

import java.time.Instant;

public record TokenRefreshResponse(
        String accessToken,
        Instant accessTokenExpiresAt
) {
    public static TokenRefreshResponse from(JwtProvider.Token token) {
        return new TokenRefreshResponse(token.token(), token.expiresAt());
    }
}
