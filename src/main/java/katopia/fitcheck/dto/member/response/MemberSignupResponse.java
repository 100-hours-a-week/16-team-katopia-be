package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.service.member.MemberRegistrationService.SignupResult;

public record MemberSignupResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,
        @Schema(description = "가입 상태", example = "ACTIVE")
        String status,
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken
) {
    public static MemberSignupResponse from(SignupResult result) {
        return new MemberSignupResponse(
                result.id(),
                result.accountStatus(),
                result.accessToken()
        );
    }
}
