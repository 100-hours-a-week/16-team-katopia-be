package katopia.fitcheck.controller;

import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.service.auth.AuthTokenService;
import katopia.fitcheck.service.auth.AuthTokenService.TokenRefreshResult;
import katopia.fitcheck.dto.auth.TokenRefreshResponse;
import katopia.fitcheck.controller.spec.AuthApiSpec;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.AuthSuccessCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthTokenController implements AuthApiSpec {

    private final AuthTokenService authTokenService;
    private final JwtProvider jwtProvider;

    @PostMapping("/tokens")
    @Override
    public ResponseEntity<APIResponse<TokenRefreshResponse>> refreshTokens(
            @CookieValue(value = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_RT);
        }

        try {
            TokenRefreshResult result = authTokenService.refreshTokens(refreshToken);
            response.addHeader(HttpHeaders.SET_COOKIE, result.refreshToken().toString());
            return APIResponse.ok(AuthSuccessCode.TOKEN_REFRESH_SUCCESS, new TokenRefreshResponse(result.accessToken()));
        } catch (AuthException ex) {
            response.addHeader(HttpHeaders.SET_COOKIE, jwtProvider.clearRefreshCookie().toString());
            throw ex;
        }
    }

    @DeleteMapping("/tokens")
    @Override
    public ResponseEntity<APIResponse<Void>> logout(
            @CookieValue(value = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (StringUtils.hasText(refreshToken)) {
            authTokenService.revokeByRefreshToken(refreshToken);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, jwtProvider.clearRefreshCookie().toString());
        return APIResponse.ok(AuthSuccessCode.LOGOUT_SUCCESS);
    }
}
