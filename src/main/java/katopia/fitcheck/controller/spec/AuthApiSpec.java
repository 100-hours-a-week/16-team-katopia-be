package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.auth.dto.TokenRefreshResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

public interface AuthApiSpec {
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
    ResponseEntity<APIResponse<TokenRefreshResponse>> refreshTokens(
            @CookieValue(value = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    );
}
