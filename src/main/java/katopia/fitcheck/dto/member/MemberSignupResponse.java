package katopia.fitcheck.dto.member;

import katopia.fitcheck.service.member.MemberRegistrationService.SignupResult;

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
