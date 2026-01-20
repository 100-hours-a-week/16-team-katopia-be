package katopia.fitcheck.global.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.AuthSuccessCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.LoginResponse;
import katopia.fitcheck.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2RegistrationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Duration WITHDRAW_REJOIN_PERIOD = Duration.ofDays(14);

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {

        // 소셜로그인만 지원. CustomOAuth2User가 아니라면 비정상 상태
        if (!(authentication.getPrincipal() instanceof CustomOAuth2User memberOAuth2User)) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_OAUTH2_PRINCIPAL);
        }

        Member member = memberOAuth2User.getMember();

        // 탈퇴 회원 처리 기준: 14일 지나면 재가입 허용
        if (member.isWithdrawn()) {
            if (member.isRejoinAllowed(WITHDRAW_REJOIN_PERIOD)) {
                member.reopenForRejoin();
                // 비활성 사용자 처리 로직으로
            } else {
                throw new AuthException(AuthErrorCode.WITHDRAWN_MEMBER);
            }
        }

        // 활성 사용자 처리
        if (!memberOAuth2User.registrationRequired()) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 비활성 사용자(신규 회원, 재가입 회원) 처리
        String registrationToken = jwtProvider.createRegistrationToken(member.getId());
        LoginResponse loginResponse = new LoginResponse(member.getAccountStatus(), member.getEmail(), registrationToken);

        ResponseEntity<APIResponse<LoginResponse>> entity =
                APIResponse.ok(AuthSuccessCode.NEW_MEMBER_NEED_INFO, loginResponse);
        response.setStatus(entity.getStatusCodeValue());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), entity.getBody());
        clearAuthenticationAttributes(request);
    }
}
