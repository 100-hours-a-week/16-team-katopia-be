package katopia.fitcheck.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import katopia.fitcheck.controller.spec.MemberApiSpec;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import katopia.fitcheck.dto.member.response.MemberFollowListResponse;
import katopia.fitcheck.dto.member.response.MemberFollowResponse;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.response.MemberSignupResponse;
import katopia.fitcheck.dto.member.response.NicknameCheckResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.AuthSuccessCode;
import katopia.fitcheck.global.exception.code.MemberSuccessCode;
import katopia.fitcheck.global.exception.code.PostSuccessCode;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.global.security.jwt.RegistrationTokenFilter;
import katopia.fitcheck.global.validation.Nickname;
import katopia.fitcheck.service.member.MemberRegistrationService;
import katopia.fitcheck.service.member.MemberService;
import katopia.fitcheck.dto.post.response.PostListResponse;
import katopia.fitcheck.service.post.PostService;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MemberController implements MemberApiSpec {

    private final MemberService memberService;
    private final PostService postService;
    private final SecuritySupport securitySupport;

    @PostMapping
    @Override
    public ResponseEntity<APIResponse<MemberSignupResponse>> signup(
            @RequestAttribute(value = RegistrationTokenFilter.REGISTRATION_MEMBER_ID, required = false) Long registrationMemberId,
            @Valid @RequestBody MemberSignupRequest request,
            HttpServletResponse response
    ) {
        if (registrationMemberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN);
        }

        MemberRegistrationService.SignupResult signupResult =
                memberService.signup(registrationMemberId, request);

        response.addHeader(HttpHeaders.SET_COOKIE, signupResult.clearRegistrationCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, signupResult.refreshCookie().toString());
        return APIResponse.ok(AuthSuccessCode.SIGNUP_SUCCESS, MemberSignupResponse.from(signupResult));
    }


    @Override
    @GetMapping("/check")
    public ResponseEntity<APIResponse<NicknameCheckResponse>> checkNickname(
            @Nickname @RequestParam("nickname") String nickname
    ) {
        NicknameCheckResponse body = memberService.checkNickname(nickname);
        return APIResponse.ok(MemberSuccessCode.NICKNAME_AVAILABLE, body);
    }


    @Override
    @GetMapping("/{memberId}")
    public ResponseEntity<APIResponse<MemberProfileResponse>> getProfile(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long memberId
    ) {
        Long requesterId = securitySupport.findMemberIdOrNull(principal);
        MemberProfileResponse responseBody = memberService.getProfile(memberId, requesterId);
        return APIResponse.ok(MemberSuccessCode.PROFILE_FETCHED, responseBody);
    }

    @Override
    @GetMapping("/{memberId}/posts")
    public ResponseEntity<APIResponse<PostListResponse>> listMemberPosts(
            @PathVariable Long memberId,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        PostListResponse responseBody = postService.listByMember(memberId, size, after);
        return APIResponse.ok(PostSuccessCode.POST_LISTED, responseBody);
    }


    @Override
    @GetMapping("/me")
    public ResponseEntity<APIResponse<MemberProfileDetailResponse>> getProfileDetail(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        MemberProfileDetailResponse responseBody = memberService.getProfileDetail(memberId);
        return APIResponse.ok(MemberSuccessCode.PROFILE_FETCHED, responseBody);
    }

    @GetMapping("/me/bookmarks")
    @Override
    public ResponseEntity<APIResponse<PostListResponse>> listMyBookmarks(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        PostListResponse responseBody = postService.listBookmarks(memberId, size, after);
        return APIResponse.ok(PostSuccessCode.POST_LISTED, responseBody);
    }


    @PatchMapping
    @Override
    public ResponseEntity<APIResponse<MemberProfileDetailResponse>> updateProfile(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody MemberProfileUpdateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        MemberProfileDetailResponse responseBody = memberService.updateProfile(memberId, request);
        return APIResponse.ok(MemberSuccessCode.PROFILE_UPDATED, responseBody);
    }


    @Override
    @DeleteMapping
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        memberService.withdraw(memberId);
        return APIResponse.noContent(AuthSuccessCode.MEMBER_WITHDRAWN);
    }

    @PostMapping("/{memberId}/follow")
    public ResponseEntity<APIResponse<MemberFollowResponse>> follow(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long memberId
    ) {
        Long followerId = securitySupport.requireMemberId(principal);
        MemberFollowResponse body = memberService.follow(followerId, memberId);
        return APIResponse.ok(MemberSuccessCode.FOLLOWED, body);
    }

    @DeleteMapping("/{memberId}/follow")
    public ResponseEntity<APIResponse<MemberFollowResponse>> unfollow(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long memberId
    ) {
        Long followerId = securitySupport.requireMemberId(principal);
        MemberFollowResponse body = memberService.unfollow(followerId, memberId);
        return APIResponse.ok(MemberSuccessCode.UNFOLLOWED, body);
    }

    @GetMapping("/{memberId}/followers")
    public ResponseEntity<APIResponse<MemberFollowListResponse>> listFollowers(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long memberId,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        securitySupport.requireMemberId(principal);
        MemberFollowListResponse responseBody = memberService.listFollowers(memberId, size, after);
        return APIResponse.ok(MemberSuccessCode.FOLLOW_LIST_FETCHED, responseBody);
    }

    @GetMapping("/{memberId}/followings")
    public ResponseEntity<APIResponse<MemberFollowListResponse>> listFollowings(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long memberId,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        securitySupport.requireMemberId(principal);
        MemberFollowListResponse responseBody = memberService.listFollowings(memberId, size, after);
        return APIResponse.ok(MemberSuccessCode.FOLLOW_LIST_FETCHED, responseBody);
    }
}
