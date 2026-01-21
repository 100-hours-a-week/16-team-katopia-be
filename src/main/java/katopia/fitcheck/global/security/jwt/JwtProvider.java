package katopia.fitcheck.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    // Header
    private static final String BEARER_PREFIX = "Bearer ";

    // Cookie
    public static final String REFRESH_COOKIE = "refresh_token";

    // Claim
    private static final String CLAIM_MEMBER_ID = "memberId";
    private static final String CLAIM_TOKEN_TYPE = "typ";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";
    private static final String TYPE_REGISTRATION = "registration";

    // Path
    public static final String REFRESH_PATH = "/api/auth/tokens";

    private final JwtProperties jwtProperties;

    private SecretKey accessSecretKey;
    private SecretKey refreshSecretKey;

    @PostConstruct
    void init() {
        String accessSecret = jwtProperties.getAccessTokenSecret();
        String refreshSecret = jwtProperties.getRefreshTokenSecret();

        Assert.hasText(accessSecret, "app.jwt.access-token-secret must be provided");
        Assert.hasText(refreshSecret, "app.jwt.refresh-token-secret must be provided");

        this.accessSecretKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**  토큰 생성 로직
     *
     * - 회원가입 임시 토큰 생성
     * - AT 생성
     * - RT 생성
     * - AT + RT 생성
     * - RT 쿠키 변환
     * - 토큰 생성
     *
     */

    // 회원가입 임시 토큰 생성
    public Token createRegistrationToken(Long memberId) {
        return buildToken(memberId, TokenType.REGISTRATION);
    }

    // AT 생성
    public Token createAccessToken(Long memberId) {
        return buildToken(memberId, TokenType.ACCESS);
    }

    // RT 생성
    public Token createRefreshToken(Long memberId) {
        return buildToken(memberId, TokenType.REFRESH);
    }

    // AT + RT 생성
    public TokenPair issueTokens(Long memberId) {
        Token accessToken = createAccessToken(memberId);
        Token refreshToken = createRefreshToken(memberId);
        return new TokenPair(accessToken, refreshToken);
    }

    // RT 쿠키 변환
    public ResponseCookie buildRefreshCookie(Token refreshToken) {
        long maxAge = Math.max(0, Duration.between(Instant.now(), refreshToken.expiresAt()).getSeconds());
        return ResponseCookie.from(REFRESH_COOKIE, refreshToken.token())
                .httpOnly(true)
                .secure(true)
                .path(REFRESH_PATH)
                .sameSite("None")
                .maxAge(maxAge)
                .build();
    }

    // 토큰 생성
    private Token buildToken(Long memberId, TokenType type) {
        Assert.notNull(memberId, "memberId must not be null");
        Instant now = Instant.now();
        Instant expiry = now.plus(switch (type) {
            case REGISTRATION -> jwtProperties.getRegistrationTokenTtl();
            case ACCESS -> jwtProperties.getAccessTokenTtl();
            case REFRESH -> jwtProperties.getRefreshTokenTtl();
        });

        String jwt = Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(String.valueOf(memberId))
                .claim(CLAIM_MEMBER_ID, memberId)
                .claim(CLAIM_TOKEN_TYPE, type.value)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey(type))
                .compact();

        return new Token(jwt, expiry);
    }

    private SecretKey signingKey(TokenType type) {
        return switch (type) {
            case ACCESS, REGISTRATION -> accessSecretKey;
            case REFRESH -> refreshSecretKey;
        };
    }

    /** 토큰 파서 및 검증
     *
     * - 토큰 타입 검증
     * - 사용자(member) 식별자 추출
     * - AT 추출
     * - 토큰 검증 및 Claims 추출
     *
     */
    public boolean isTokenType(String token, TokenType type) {
        return token != null && parseClaims(token, type) != null;
    }


    public Long extractMemberId(String token, TokenType type) {
        Claims claims = parseClaims(token, type);
        if (claims == null) {
            return null;
        }
        Number memberId = claims.get(CLAIM_MEMBER_ID, Number.class);
        return memberId != null ? memberId.longValue() : null;
    }


    public String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_PREFIX.length());
    }


    private Claims parseClaims(String token, TokenType type) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey(type))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String claimType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            if (claimType == null || !type.matches(claimType)) {
                return null;
            }
            return claims;
        } catch (Exception ex) {
            return null;
        }
    }

    public enum TokenType {
        ACCESS(TYPE_ACCESS),
        REFRESH(TYPE_REFRESH),
        REGISTRATION(TYPE_REGISTRATION);

        private final String value;

        TokenType(String value) {
            this.value = value;
        }

        boolean matches(String value) {
            return this.value.equals(value);
        }
    }

    public record Token(String token, Instant expiresAt) { }

    public record TokenPair(Token accessToken, Token refreshToken) { }
}
