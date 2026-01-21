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

    private static final String REGISTRATION_ENDPOINT = "/api/members";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        boolean isRegistrationEndpoint = isRegistrationRequest(request);
        String accessToken = jwtProvider.extractBearerToken(request);

        if (!isRegistrationEndpoint) {
            // 회원가입 API 요청이 아닌데, 임시 AT인 경우
            if (accessToken != null && jwtProvider.isTokenType(accessToken, TokenType.REGISTRATION)) {
                throw new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN_PATH);
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken == null) { // isRegistrationEndpoint && accessToken == null
            throw new AuthException(AuthErrorCode.NOT_FOUND_AT);
        }

        Long memberId = jwtProvider.extractMemberId(accessToken, JwtProvider.TokenType.REGISTRATION);
        if (memberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN);
        }
        request.setAttribute(REGISTRATION_MEMBER_ID, memberId);
        filterChain.doFilter(request, response);
    }

    private boolean isRegistrationRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && PATH_MATCHER.match(REGISTRATION_ENDPOINT, request.getRequestURI());
    }
}
