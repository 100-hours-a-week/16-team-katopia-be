package katopia.fitcheck.member.service;

import katopia.fitcheck.member.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRegistrationService memberRegistrationService;
    private final MemberProfileService memberProfileService;

    @Transactional
    public SignupWithCookie signup(Long registrationMemberId, String nickname) {
        MemberRegistrationService.SignupResult signupResult =
                memberRegistrationService.signup(registrationMemberId, nickname);
        ResponseCookie cookie = memberRegistrationService.buildRefreshCookie(signupResult.refreshToken());
        return new SignupWithCookie(signupResult, cookie);
    }

    public MemberProfileDetailResponse updateProfile(Long memberId, MemberProfileUpdateRequest request) {
        return memberProfileService.updateProfile(memberId, request);
    }

    public MemberProfileResponse getProfile(Long memberId) {
        return memberProfileService.getProfile(memberId);
    }

    public MemberProfileDetailResponse getProfileDetail(Long memberId) {
        return memberProfileService.getProfileDetail(memberId);
    }

    public void withdraw(Long memberId) {
        memberProfileService.withdraw(memberId);
    }

    public ResponseCookie clearRegistrationCookie() {
        return memberRegistrationService.clearRegistrationCookie();
    }

    public NicknameDuplicateCheckResponse checkNickname(String nickname) {
        return memberProfileService.checkNickname(nickname);
    }

    public record SignupWithCookie(
            MemberRegistrationService.SignupResult signupResult,
            ResponseCookie refreshCookie) { }
}
