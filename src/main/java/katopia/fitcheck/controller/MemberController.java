package katopia.fitcheck.controller;

import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.controller.spec.MemberApiSpec;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.AuthSuccessCode;
import katopia.fitcheck.global.exception.code.MemberSuccessCode;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.global.security.jwt.RegistrationTokenFilter;
import katopia.fitcheck.member.dto.*;
import katopia.fitcheck.member.service.MemberService;
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
public class MemberController implements MemberApiSpec {

    private final MemberService memberService;
    private final SecuritySupport securitySupport;

    @PostMapping
    @Override
    public ResponseEntity<APIResponse<MemberSignupResponse>> signup(
            @RequestAttribute(value = RegistrationTokenFilter.REGISTRATION_MEMBER_ID, required = false) Long registrationMemberId,
            @RequestBody MemberSignupRequest request,
            HttpServletResponse response
    ) {
        if (registrationMemberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN);
        }

        MemberService.SignupWithCookie payload = memberService.signup(registrationMemberId, request.nickname());
        response.addHeader(HttpHeaders.SET_COOKIE, payload.refreshCookie().toString());
        return APIResponse.ok(AuthSuccessCode.LOGIN_SUCCESS, MemberSignupResponse.from(payload.signupResult()));
    }


    @Override
    @GetMapping("/check")
    public ResponseEntity<APIResponse<NicknameDuplicateCheckResponse>> checkNickname(
            @RequestParam("nickname") String nickname
    ) {
        NicknameDuplicateCheckResponse body = memberService.checkNickname(nickname);
        return APIResponse.ok(MemberSuccessCode.NICKNAME_AVAILABLE, body);
    }


    @Override
    @GetMapping("/{memberId}")
    public ResponseEntity<APIResponse<MemberProfileResponse>> getProfile(
            @PathVariable Long memberId
    ) {
        MemberProfileResponse responseBody = memberService.getProfile(memberId);
        return APIResponse.ok(MemberSuccessCode.PROFILE_FETCHED, responseBody);
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


    @PatchMapping
    @Override
    public ResponseEntity<APIResponse<MemberProfileDetailResponse>> updateProfile(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody MemberProfileUpdateRequest request
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
}
