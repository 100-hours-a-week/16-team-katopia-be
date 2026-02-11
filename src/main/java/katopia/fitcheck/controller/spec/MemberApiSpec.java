package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.global.security.jwt.RegistrationTokenFilter;
import katopia.fitcheck.global.validation.Nickname;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import katopia.fitcheck.dto.member.response.MemberFollowListResponse;
import katopia.fitcheck.dto.member.response.MemberFollowResponse;
import katopia.fitcheck.dto.member.response.MemberSignupResponse;
import katopia.fitcheck.dto.member.response.NicknameCheckResponse;
import katopia.fitcheck.dto.post.response.PostListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface MemberApiSpec {

    @Operation(
            summary = "회원가입",
            description = "소셜로그인 후 비활성 사용자에게 발급된 임시 인증 쿠키로 회원가입을 진행합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 완료", content = @Content(schema = @Schema(implementation = MemberSignupResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = "재로그인 필요. 임시 인증 쿠키 누락/만료/위조", content = @Content)
    @ApiResponse(responseCode = "409", description = "이미 사용중인 닉네임", content = @Content)
    @PostMapping
    ResponseEntity<APIResponse<MemberSignupResponse>> signup(
            @RequestAttribute(value = RegistrationTokenFilter.REGISTRATION_MEMBER_ID, required = false) Long registrationMemberId,
            @Valid @RequestBody MemberSignupRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "닉네임 유효성/중복 확인",
            description = "nickname 쿼리 파라미터의 형식을 검증한 뒤, 사용 가능하면 `isAvailable=true`를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "닉네임 사용 가능 여부 반환", content = @Content(schema = @Schema(implementation = NicknameCheckResponse.class)))
    @ApiResponse(responseCode = Docs.INPUT_VALIDATION_DES, description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @GetMapping("/check")
    ResponseEntity<APIResponse<NicknameCheckResponse>> checkNickname(
            @Nickname @RequestParam("nickname") String nickname
    );


    @Operation(
            summary = "사용자 공개 정보 조회",
            description = "memberId를 기준으로 공개 프로필 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공", content = @Content(schema = @Schema(implementation = MemberProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @GetMapping("/{memberId}")
    ResponseEntity<APIResponse<MemberProfileResponse>> getProfile(
            @PathVariable Long memberId
    );

    @Operation(
            summary = "사용자 게시글 조회",
            description = "특정 사용자가 작성한 게시글을 커서 기반으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "사용자 게시글 조회 성공", content = @Content(schema = @Schema(implementation = PostListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @GetMapping("/{memberId}/posts")
    ResponseEntity<APIResponse<PostListResponse>> listMemberPosts(
            @PathVariable Long memberId,
            @Parameter(description = "페이지 크기")
            @RequestParam(value = "size", required = false) String size,
            @Parameter(description = "커서 (createdAt|id 형식)")
            @RequestParam(value = "after", required = false) String after
    );


    @Operation(
            summary = "내 정보 조회",
            description = "인증된 사용자의 상세 프로필(닉네임/프로필 이미지/신체 정보 등)을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "자기 정보 조회 성공", content = @Content(schema = @Schema(implementation = MemberProfileDetailResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @GetMapping("/me")
    ResponseEntity<APIResponse<MemberProfileDetailResponse>> getProfileDetail(
            @AuthenticationPrincipal MemberPrincipal principal
    );

    @Operation(summary = "내 북마크 목록", description = "내가 북마크한 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "북마크 목록 조회 성공", content = @Content(schema = @Schema(implementation = PostListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @GetMapping("/me/bookmarks")
    ResponseEntity<APIResponse<PostListResponse>> listMyBookmarks(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "내 정보 수정", description = "인증된 사용자의 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "내 정보 수정 성공", content = @Content(schema = @Schema(implementation = MemberProfileDetailResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @ApiResponse(responseCode = "409", description = "이미 사용중인 닉네임", content = @Content)
    @PatchMapping
    ResponseEntity<APIResponse<MemberProfileDetailResponse>> updateProfile(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody MemberProfileUpdateRequest request
    );


    // DELETE
    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "204", description = "탈퇴 성공")
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @DeleteMapping
    ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal MemberPrincipal principal
    );

    // FOLLOW
    @Operation(summary = "팔로우")
    @ApiResponse(responseCode = "201", description = "팔로우 성공", content = @Content(schema = @Schema(implementation = MemberFollowResponse.class)))
    @ApiResponse(responseCode = "400", description = "자기 자신 팔로우 불가", content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "409", description = "이미 팔로우한 사용자", content = @Content)
    @PostMapping("/{memberId}/follow")
    ResponseEntity<APIResponse<MemberFollowResponse>> follow(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.ID_DES, example = "1")
            @PathVariable Long memberId
    );

    @Operation(summary = "언팔로우")
    @ApiResponse(responseCode = "200", description = "언팔로우 성공", content = @Content(schema = @Schema(implementation = MemberFollowResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @DeleteMapping("/{memberId}/follow")
    ResponseEntity<APIResponse<MemberFollowResponse>> unfollow(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.ID_DES, example = "1")
            @PathVariable Long memberId
    );

    @Operation(summary = "팔로워 목록 조회")
    @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공", content = @Content(schema = @Schema(implementation = MemberFollowListResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @GetMapping("/{memberId}/followers")
    ResponseEntity<APIResponse<MemberFollowListResponse>> listFollowers(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.ID_DES, example = "1")
            @PathVariable Long memberId,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "팔로잉 목록 조회")
    @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공", content = @Content(schema = @Schema(implementation = MemberFollowListResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @GetMapping("/{memberId}/followings")
    ResponseEntity<APIResponse<MemberFollowListResponse>> listFollowings(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.ID_DES, example = "1")
            @PathVariable Long memberId,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String after
    );
}
