package katopia.fitcheck.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RegistrationTokenFilter extends OncePerRequestFilter {

    public static final String REGISTRATION_MEMBER_ID = "registrationMemberId";

    private static final AllowEndpoint REGISTRATION_ENDPOINT = new AllowEndpoint("/api/members", "POST"),
            REGISTRATION_CHECK_ENDPOINT = new AllowEndpoint("/api/members/check", "GET"),
            MEMBER_ME_ENDPOINT = new AllowEndpoint("/api/members/me", "GET");

    private final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        boolean isRegistrationEndpoint = isAllowEndPoint(request, REGISTRATION_ENDPOINT);
        String registrationToken = jwtProvider.extractCookieValue(request, JwtProvider.REGISTRATION_COOKIE);

        /*
        회원가입 API 엔드포인트가 아닌 경우
         */
        if (!isRegistrationEndpoint) {
            /*
            닉네임 유효성 검증 / 내 정보 조회는 가능
             */
            if (
                isAllowEndPoint(request, REGISTRATION_CHECK_ENDPOINT) ||
                isAllowEndPoint(request, MEMBER_ME_ENDPOINT)
            ) {
                filterChain.doFilter(request, response);
                return;
            }
            /*
            그 외의 엔드포인트에 대해 회원가입용 토큰(쿠키)인 경우 차단.
             */
            if (registrationToken != null && jwtProvider.isTokenType(registrationToken, TokenType.REGISTRATION)) {
                response.addHeader("Set-Cookie", jwtProvider.clearRegistrationCookie().toString());
                throw new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN_PATH);
            }
            filterChain.doFilter(request, response);
            return;
        } else if (registrationToken == null) { // 회원가입 API 호출인데 임시 토큰이 없다면 요청 선제적 차단
            throw new AuthException(AuthErrorCode.NOT_FOUND_TEMP_TOKEN);
        }

        Long memberId = jwtProvider.extractMemberId(registrationToken, JwtProvider.TokenType.REGISTRATION);
        if (memberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN);
        }
        request.setAttribute(REGISTRATION_MEMBER_ID, memberId);
        filterChain.doFilter(request, response);
    }

    private boolean isAllowEndPoint(HttpServletRequest request, AllowEndpoint allowEndpoint) {
        return allowEndpoint.method.equalsIgnoreCase(request.getMethod())
                && PATH_MATCHER.match(allowEndpoint.endPoint, request.getRequestURI());
    }

    private record AllowEndpoint(
        String endPoint,
        String method
    ) {}
}
