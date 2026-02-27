package katopia.fitcheck.global.security.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import katopia.fitcheck.global.security.oauth2.FrontendProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private static final String ACCESS_SECRET = "access-secret-0123456789-0123456789-0123456789";
    private static final String REFRESH_SECRET = "refresh-secret-0123456789-0123456789-012345678";

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setAccessTokenSecret(ACCESS_SECRET);
        jwtProperties.setRefreshTokenSecret(REFRESH_SECRET);
        jwtProperties.setAccessTokenTtl(Duration.ofMinutes(5));
        jwtProperties.setRefreshTokenTtl(Duration.ofDays(1));
        jwtProperties.setRegistrationTokenTtl(Duration.ofMinutes(10));

        FrontendProperties frontendProperties = new FrontendProperties();
        frontendProperties.setBaseUrl("http://localhost:3000");

        jwtProvider = new JwtProvider(jwtProperties, frontendProperties);
        jwtProvider.init();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-01 유효 토큰에서 memberId 추출 성공")
    void tcJwtS01_extractMemberId_returnsMemberId_forValidAccessToken() {
        JwtProvider.Token token = jwtProvider.createAccessToken(1L);

        assertThat(jwtProvider.isTokenType(token.token(), JwtProvider.TokenType.ACCESS)).isTrue();
        assertThat(jwtProvider.extractMemberId(token.token(), JwtProvider.TokenType.ACCESS)).isEqualTo(1L);
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-02 Bearer 헤더 추출 성공")
    void tcJwtS02_extractBearerToken_returnsToken_whenHeaderIsBearer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");

        assertThat(jwtProvider.extractBearerToken(request)).isEqualTo("test-token");
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-03 쿠키 값 추출 성공")
    void tcJwtS03_extractCookieValue_returnsCookieValue_whenPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refresh_token", "value"));

        assertThat(jwtProvider.extractCookieValue(request, "refresh_token")).isEqualTo("value");
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-04 Access 토큰 발급 성공")
    void tcJwtS04_createAccessToken_issuesAccessToken() {
        JwtProvider.Token token = jwtProvider.createAccessToken(1L);

        assertThat(jwtProvider.isTokenType(token.token(), JwtProvider.TokenType.ACCESS)).isTrue();
        assertThat(token.expiresAt()).isAfter(Instant.now());
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-05 Refresh 토큰 발급 성공")
    void tcJwtS05_createRefreshToken_issuesRefreshToken() {
        JwtProvider.Token token = jwtProvider.createRefreshToken(1L);

        assertThat(jwtProvider.isTokenType(token.token(), JwtProvider.TokenType.REFRESH)).isTrue();
        assertThat(token.expiresAt()).isAfter(Instant.now());
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-06 Registration 토큰 발급 성공")
    void tcJwtS06_createRegistrationToken_issuesRegistrationToken() {
        JwtProvider.Token token = jwtProvider.createRegistrationToken(1L);

        assertThat(jwtProvider.isTokenType(token.token(), JwtProvider.TokenType.REGISTRATION)).isTrue();
        assertThat(token.expiresAt()).isAfter(Instant.now());
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-07 AT/RT 동시 발급 성공")
    void tcJwtS07_issueTokens_issuesAccessAndRefresh() {
        JwtProvider.TokenPair tokenPair = jwtProvider.issueTokens(1L);

        assertThat(jwtProvider.isTokenType(tokenPair.accessToken().token(), JwtProvider.TokenType.ACCESS)).isTrue();
        assertThat(jwtProvider.isTokenType(tokenPair.refreshToken().token(), JwtProvider.TokenType.REFRESH)).isTrue();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-08 Refresh 쿠키 생성")
    void tcJwtS08_buildRefreshCookie_buildsCookie() {
        JwtProvider.Token refreshToken = jwtProvider.createRefreshToken(1L);

        ResponseCookie cookie = jwtProvider.buildRefreshCookie(refreshToken);

        assertThat(cookie.getName()).isEqualTo(JwtProvider.REFRESH_COOKIE);
        assertThat(cookie.getPath()).isEqualTo(JwtProvider.REFRESH_PATH);
        assertThat(cookie.getMaxAge()).isPositive();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-09 Registration 쿠키 생성")
    void tcJwtS09_buildRegistrationCookie_buildsCookie() {
        JwtProvider.Token registrationToken = jwtProvider.createRegistrationToken(1L);

        ResponseCookie cookie = jwtProvider.buildRegistrationCookie(registrationToken);

        assertThat(cookie.getName()).isEqualTo(JwtProvider.REGISTRATION_COOKIE);
        assertThat(cookie.getPath()).isEqualTo(JwtProvider.REGISTRATION_PATH);
        assertThat(cookie.getMaxAge()).isPositive();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-10 Refresh 쿠키 삭제")
    void tcJwtS10_clearRefreshCookie_expiresCookie() {
        ResponseCookie cookie = jwtProvider.clearRefreshCookie();

        assertThat(cookie.getName()).isEqualTo(JwtProvider.REFRESH_COOKIE);
        assertThat(cookie.getMaxAge()).isZero();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-S-11 Registration 쿠키 삭제")
    void tcJwtS11_clearRegistrationCookie_expiresCookie() {
        ResponseCookie cookie = jwtProvider.clearRegistrationCookie();

        assertThat(cookie.getName()).isEqualTo(JwtProvider.REGISTRATION_COOKIE);
        assertThat(cookie.getMaxAge()).isZero();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-01 만료 토큰 검증 실패")
    void tcJwtF01_extractMemberId_returnsNull_forExpiredToken() {
        String token = buildToken(1L, JwtProvider.TokenType.ACCESS, ACCESS_SECRET,
                Instant.now().minusSeconds(120), Instant.now().minusSeconds(60), true, true);

        assertThat(jwtProvider.isTokenType(token, JwtProvider.TokenType.ACCESS)).isFalse();
        assertThat(jwtProvider.extractMemberId(token, JwtProvider.TokenType.ACCESS)).isNull();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-02 서명 불일치 토큰 실패")
    void tcJwtF02_extractMemberId_returnsNull_forSignatureMismatch() {
        String token = buildToken(1L, JwtProvider.TokenType.ACCESS, "different-secret-0123456789-0123456789-012345",
                Instant.now(), Instant.now().plusSeconds(300), true, true);

        assertThat(jwtProvider.isTokenType(token, JwtProvider.TokenType.ACCESS)).isFalse();
        assertThat(jwtProvider.extractMemberId(token, JwtProvider.TokenType.ACCESS)).isNull();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-03 토큰 타입 불일치 실패")
    void tcJwtF03_extractMemberId_returnsNull_forWrongTokenType() {
        JwtProvider.Token token = jwtProvider.createAccessToken(1L);

        assertThat(jwtProvider.isTokenType(token.token(), JwtProvider.TokenType.REFRESH)).isFalse();
        assertThat(jwtProvider.extractMemberId(token.token(), JwtProvider.TokenType.REFRESH)).isNull();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-04 typ 누락 토큰 실패")
    void tcJwtF04_extractMemberId_returnsNull_whenTypMissing() {
        String token = buildToken(1L, JwtProvider.TokenType.ACCESS, ACCESS_SECRET,
                Instant.now(), Instant.now().plusSeconds(300), false, true);

        assertThat(jwtProvider.isTokenType(token, JwtProvider.TokenType.ACCESS)).isFalse();
        assertThat(jwtProvider.extractMemberId(token, JwtProvider.TokenType.ACCESS)).isNull();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-05 memberId 누락 토큰 실패")
    void tcJwtF05_extractMemberId_returnsNull_whenMemberIdMissing() {
        String token = buildToken(null, JwtProvider.TokenType.ACCESS, ACCESS_SECRET,
                Instant.now(), Instant.now().plusSeconds(300), true, false);

        assertThat(jwtProvider.isTokenType(token, JwtProvider.TokenType.ACCESS)).isTrue();
        assertThat(jwtProvider.extractMemberId(token, JwtProvider.TokenType.ACCESS)).isNull();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-06 잘못된 형식 토큰 실패")
    void tcJwtF06_extractMemberId_returnsNull_forMalformedToken() {
        assertThat(jwtProvider.isTokenType("not-a-token", JwtProvider.TokenType.ACCESS)).isFalse();
        assertThat(jwtProvider.extractMemberId("not-a-token", JwtProvider.TokenType.ACCESS)).isNull();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-07 Bearer 헤더 누락/형식 오류")
    void tcJwtF07_extractBearerToken_returnsNull_whenHeaderMissingOrInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThat(jwtProvider.extractBearerToken(request)).isNull();

        request.addHeader("Authorization", "Basic test-token");
        assertThat(jwtProvider.extractBearerToken(request)).isNull();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("TC-JWT-F-08 쿠키 값 추출 실패")
    void tcJwtF08_extractCookieValue_returnsNull_whenMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThat(jwtProvider.extractCookieValue(request, "missing")).isNull();
    }

    private String buildToken(Long memberId,
                              JwtProvider.TokenType type,
                              String secret,
                              Instant issuedAt,
                              Instant expiresAt,
                              boolean includeTyp,
                              boolean includeMemberId) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        JwtBuilder builder = Jwts.builder()
                .issuer("fit-check")
                .subject(memberId != null ? String.valueOf(memberId) : "0")
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(key);

        if (includeTyp) {
            builder.claim("typ", typeValue(type));
        }
        if (includeMemberId && memberId != null) {
            builder.claim("memberId", memberId);
        }
        return builder.compact();
    }

    private String typeValue(JwtProvider.TokenType type) {
        return switch (type) {
            case ACCESS -> "access";
            case REFRESH -> "refresh";
            case REGISTRATION -> "registration";
        };
    }
}
