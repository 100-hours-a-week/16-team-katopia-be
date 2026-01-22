package katopia.fitcheck.global.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final String WITHDRAWN_MESSAGE_TEMPLATE =
            "탈퇴한 계정입니다. 14일 이후 재가입 가능합니다.(가입 가능 날짜: %s)";

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        ResponseCode code = AuthErrorCode.INVALID_AT;
        if (exception instanceof AuthException authException) {
            code = authException.getErrorCode();
        }

        String message = code.getMessage();
        Object rejoinAt = request.getAttribute(OAuth2RegistrationSuccessHandler.WITHDRAWN_REJOIN_AT_ATTR);
        if (code == AuthErrorCode.WITHDRAWN_MEMBER && rejoinAt instanceof String rejoinAtValue) {
            message = String.format(WITHDRAWN_MESSAGE_TEMPLATE, rejoinAtValue);
        }

        ResponseEntity<APIResponse<?>> entity = APIResponse.error(code, message);

        response.setStatus(code.getStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(entity.getBody()));
    }
}
