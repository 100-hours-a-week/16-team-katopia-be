package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.global.security.jwt.RegistrationTokenFilter;
import katopia.fitcheck.global.validation.Nickname;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.request.MemberSignupRequest;
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

    // CREATE
    @Operation(
            summary = "회원가입 완료",
            description = "소셜 로그인으로 발급된 등록 토큰(쿠키)과 닉네임으로 회원을 생성하고 Refresh Token을 쿠키로 발급합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 완료", content = @Content(schema = @Schema(implementation = MemberSignupResponse.class)))
    @ApiResponse(responseCode = "400", description = "닉네임 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "등록 토큰 누락/만료", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "409", description = "중복 닉네임 또는 이미 가입된 계정", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @PostMapping
    ResponseEntity<APIResponse<MemberSignupResponse>> signup(
            @RequestAttribute(value = RegistrationTokenFilter.REGISTRATION_MEMBER_ID, required = false) Long registrationMemberId,
            @Valid @RequestBody MemberSignupRequest request,
            HttpServletResponse response
    );


    // READ
    @Operation(
            summary = "닉네임 유효성/중복 확인",
            description = "nickname 쿼리 파라미터의 형식을 검증한 뒤, 사용 가능하면 `isAvailable=true`를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "닉네임 사용 가능 여부 반환", content = @Content(schema = @Schema(implementation = NicknameCheckResponse.class)))
    @ApiResponse(responseCode = "400", description = "닉네임 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @GetMapping("/check")
    ResponseEntity<APIResponse<NicknameCheckResponse>> checkNickname(
            @Nickname @RequestParam("nickname") String nickname
    );


    @Operation(
            summary = "사용자 정보 조회",
            description = "memberId를 기준으로 공개 프로필 정보(닉네임/스타일/통계)를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공", content = @Content(schema = @Schema(implementation = MemberProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "식별자 포맷 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "해당 사용자 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @GetMapping("/{memberId}")
    ResponseEntity<APIResponse<MemberProfileResponse>> getProfile(
            @PathVariable Long memberId
    );

    @Operation(
            summary = "사용자 게시글 조회",
            description = "특정 사용자가 작성한 게시글을 커서 기반으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "사용자 게시글 조회 성공", content = @Content(schema = @Schema(implementation = PostListResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "해당 사용자 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
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
    @ApiResponse(responseCode = "401", description = "인증 정보 부재", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @GetMapping("/me")
    ResponseEntity<APIResponse<MemberProfileDetailResponse>> getProfileDetail(
            @AuthenticationPrincipal MemberPrincipal principal
    );


    // UPDATE
    @Operation(summary = "내 정보 수정")
    @PatchMapping
    ResponseEntity<APIResponse<MemberProfileDetailResponse>> updateProfile(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody MemberProfileUpdateRequest request
    );


    // DELETE
    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "204", description = "탈퇴 성공")
    @ApiResponse(responseCode = "401", description = "인증 정보 부재", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "403", description = "이미 탈퇴된 계정", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @DeleteMapping
    ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal MemberPrincipal principal
    );
}
