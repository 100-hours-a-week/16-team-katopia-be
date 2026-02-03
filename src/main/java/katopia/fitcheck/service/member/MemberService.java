package katopia.fitcheck.service.member;

import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.response.NicknameCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRegistrationService memberRegistrationService;
    private final MemberProfileService memberProfileService;

    public MemberRegistrationService.SignupResult signup(Long registrationMemberId, MemberSignupRequest request) {
        return memberRegistrationService.signup(registrationMemberId, request);
    }

    public NicknameCheckResponse checkNickname(String nickname) {
        return memberProfileService.checkNickname(nickname);
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
}
