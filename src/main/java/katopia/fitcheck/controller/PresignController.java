package katopia.fitcheck.controller;

import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.CommonSuccessCode;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.service.s3.PresignService;
import katopia.fitcheck.dto.s3.PresignRequest;
import katopia.fitcheck.dto.s3.PresignResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class PresignController {

    private final PresignService presignService;
    private final SecuritySupport securitySupport;

    @PostMapping("/presign")
    @Operation(summary = "이미지 업로드 URL 발급", description = "프로필/게시글/투표 이미지 업로드를 위한 presigned URL을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "발급 성공", content = @Content(schema = @Schema(implementation = PresignResponse.class)))
    @ApiResponse(responseCode = "400", description = "요청 값 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 정보 부재", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    public ResponseEntity<APIResponse<PresignResponse>> presign(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody PresignRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        PresignResponse response = presignService.createPresignedUrls(memberId, request);
        return APIResponse.ok(CommonSuccessCode.PRESIGN_ISSUED, response);
    }
}
