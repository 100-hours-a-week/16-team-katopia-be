package katopia.fitcheck.member.dto;

import katopia.fitcheck.member.service.MemberRegistrationService.SignupResult;

public record MemberSignupResponse(
        String status,
        String accessToken
) {

    public static MemberSignupResponse from(SignupResult result) {
        return new MemberSignupResponse(
                result.member().getAccountStatus().name(),
                result.accessToken().token()
        );
    }
}
