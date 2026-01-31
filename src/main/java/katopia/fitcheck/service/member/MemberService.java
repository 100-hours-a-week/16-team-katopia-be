package katopia.fitcheck.service.member;

import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.response.NicknameCheckResponse;
import katopia.fitcheck.service.member.MemberRegistrationService.SignupResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRegistrationService memberRegistrationService;
    private final MemberProfileService memberProfileService;

    public MemberRegistrationService.SignupResult signup(Long registrationMemberId, String nickname, String gender) {
        return memberRegistrationService.signup(registrationMemberId, nickname, gender);
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
