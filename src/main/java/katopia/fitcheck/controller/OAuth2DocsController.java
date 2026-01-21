package katopia.fitcheck.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping
public class OAuth2DocsController {

    @Operation(summary = "카카오 로그인 시작", description = "Swagger 문서용 경로입니다. 브라우저에서 직접 호출해주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "카카오 인증 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "501", description = "Swagger 문서용(실행 불가)")
    })
    @GetMapping("/oauth2/authorization/kakao")
    public void kakaoLoginDocs(HttpServletResponse response) throws IOException {
        response.sendError(501, "Swagger 문서용 엔드포인트입니다. 브라우저에서 호출해주세요.");
    }
}