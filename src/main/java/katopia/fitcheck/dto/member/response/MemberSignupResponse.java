package katopia.fitcheck.dto.member.response;

import katopia.fitcheck.service.member.MemberRegistrationService.SignupResult;

public record MemberSignupResponse(
        Long id,
        String status,
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
