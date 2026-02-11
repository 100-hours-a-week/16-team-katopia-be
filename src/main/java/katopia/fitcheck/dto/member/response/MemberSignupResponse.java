package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.service.member.MemberRegistrationService.SignupResult;

public record MemberSignupResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = "가입 상태", example = "ACTIVE")
        String status,
        @Schema(description = Docs.AT_DES, example = Docs.AT)
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
