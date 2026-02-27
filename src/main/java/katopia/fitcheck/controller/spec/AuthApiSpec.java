package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.dto.auth.TokenRefreshResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

public interface AuthApiSpec {
    @PostMapping("/tokens")
    @Operation(summary = "토큰 재발급(RTR)", description = "RTR 전략에 따라 AT/RT를 모두 재발급합니다. 이전 RT는 만료(maxAge=0) 및 폐기되어 사용이 불가능해집니다.")
    @ApiResponse(responseCode = "200", description = "AT/RT 재발급 성공", content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.RT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<TokenRefreshResponse>> refreshTokens(
            @CookieValue(value = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    );

    @DeleteMapping("/tokens")
    @Operation(summary = "로그아웃", description = "서비스 RT 쿠키를 만료(maxAge=0) 처리합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<Void>> logout(
            @CookieValue(value = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    );
}
