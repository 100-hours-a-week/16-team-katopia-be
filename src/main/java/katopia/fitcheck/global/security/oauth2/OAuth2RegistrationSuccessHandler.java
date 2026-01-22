package katopia.fitcheck.global.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OAuth2RegistrationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Duration WITHDRAW_REJOIN_PERIOD = Duration.ofDays(14);
    public static final String WITHDRAWN_REJOIN_AT_ATTR = "withdrawnRejoinAt";
    private static final DateTimeFormatter REJOIN_AT_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final JwtProvider jwtProvider;
    private final FrontendProperties frontendProperties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {

        // 소셜로그인만 지원. CustomOAuth2User가 아니라면 비정상 상태
        if (!(authentication.getPrincipal() instanceof CustomOAuth2User memberOAuth2User)) {
            clearAuthenticationAttributes(request);
            redirectToHome(request, response);
            return;
        }

        Member member = memberOAuth2User.getMember();

        // 탈퇴 회원 처리 기준: 14일 지나면 재가입 허용
        if (member.isWithdrawn()) {
            if (member.isRejoinAllowed(WITHDRAW_REJOIN_PERIOD)) {
                member.reopenForRejoin();
                // 비활성 사용자 처리 로직으로
            } else {
                Instant rejoinAvailableAt = member.rejoinAvailableAt(WITHDRAW_REJOIN_PERIOD);
                if (rejoinAvailableAt != null) {
                    request.setAttribute(
                            WITHDRAWN_REJOIN_AT_ATTR,
                            REJOIN_AT_FORMATTER.format(rejoinAvailableAt)
                    );
                }
                clearAuthenticationAttributes(request);
                redirectToHome(request, response);
                return;
            }
        }

        // 활성 사용자 처리
        if (!memberOAuth2User.registrationRequired()) {
            var tokens = jwtProvider.issueTokens(member.getId());
            response.addHeader(HttpHeaders.SET_COOKIE,
                    jwtProvider.buildRefreshCookie(tokens.refreshToken()).toString());
            clearAuthenticationAttributes(request);
            redirectToHome(request, response);
            return;
        }

        // 비활성 사용자(신규 회원, 재가입 회원) 처리
        var registrationToken = jwtProvider.createRegistrationToken(member.getId());
        response.addHeader(HttpHeaders.SET_COOKIE,
                jwtProvider.buildRegistrationCookie(registrationToken).toString());
        clearAuthenticationAttributes(request);
        redirectToSignup(request, response);
    }

    private void redirectToHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseUrl = frontendProperties.getBaseUrl();
        getRedirectStrategy().sendRedirect(request, response, normalizeBaseUrl(baseUrl) + "/home");
    }

    private void redirectToSignup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseUrl = frontendProperties.getBaseUrl();
        getRedirectStrategy().sendRedirect(request, response, normalizeBaseUrl(baseUrl) + "/signup/step1");
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
