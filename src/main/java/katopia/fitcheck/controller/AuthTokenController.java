package katopia.fitcheck.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.auth.AuthTokenService;
import katopia.fitcheck.auth.AuthTokenService.TokenRefreshResult;
import katopia.fitcheck.auth.dto.TokenRefreshResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthTokenController{

    private final AuthTokenService authTokenService;

    @PostMapping("/tokens")
    @Operation(summary = "토큰 재발급", description = "RTR 규칙에 따라 AT/RT를 모두 재발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재발급 성공",
                            content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))),
                    @ApiResponse(responseCode = "401", description = "RT 쿠키 누락",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "401", description = "RT 만료/위조",
                            content = @Content(schema = @Schema(implementation = APIResponse.class)))
            })
    public ResponseEntity<APIResponse<TokenRefreshResponse>> refreshTokens(
            @CookieValue(value = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_RT);
        }

        TokenRefreshResult result = authTokenService.refreshTokens(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, result.refreshToken().toString());

        return APIResponse.ok(AuthSuccessCode.TOKEN_REFRESH_SUCCESS, new TokenRefreshResponse(result.accessToken()));
    }
}
