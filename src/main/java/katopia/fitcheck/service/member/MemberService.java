package katopia.fitcheck.service.member;

import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.response.MemberFollowResponse;
import katopia.fitcheck.dto.member.response.MemberFollowListResponse;
import katopia.fitcheck.dto.member.response.NicknameCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRegistrationService memberRegistrationService;
    private final MemberProfileService memberProfileService;
    private final MemberFollowService memberFollowService;

    public MemberRegistrationService.SignupResult signup(Long registrationMemberId, MemberSignupRequest request) {
        return memberRegistrationService.signup(registrationMemberId, request);
    }

    public NicknameCheckResponse checkNickname(String nickname) {
        return memberProfileService.checkNickname(nickname);
    }

    public MemberProfileDetailResponse updateProfile(Long memberId, MemberProfileUpdateRequest request) {
        return memberProfileService.updateProfile(memberId, request);
    }

    public MemberProfileResponse getProfile(Long memberId, Long requesterId) {
        return memberProfileService.getProfile(memberId, requesterId);
    }

    public MemberProfileDetailResponse getProfileDetail(Long memberId) {
        return memberProfileService.getProfileDetail(memberId);
    }

    public void withdraw(Long memberId) {
        memberProfileService.withdraw(memberId);
    }

    public MemberFollowResponse follow(Long followerId, Long followedId) {
        return memberFollowService.follow(followerId, followedId);
    }

    public MemberFollowResponse unfollow(Long followerId, Long followedId) {
        return memberFollowService.unfollow(followerId, followedId);
    }

    public MemberFollowListResponse listFollowers(Long memberId, String sizeValue, String after) {
        return memberFollowService.listFollowers(memberId, sizeValue, after);
    }

    public MemberFollowListResponse listFollowings(Long memberId, String sizeValue, String after) {
        return memberFollowService.listFollowings(memberId, sizeValue, after);
    }
}
