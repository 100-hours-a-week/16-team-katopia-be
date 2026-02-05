package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.dto.search.PostSearchResponse;
import katopia.fitcheck.dto.search.MemberSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

public interface SearchApiSpec {

    @Operation(summary = "계정 검색", description = "로그인 사용자 전용 계정 검색 API입니다. 닉네임 prefix 검색과 커서 기반 인피니티 스크롤을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = MemberSearchResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 필수 입력", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 길이 제한", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "커서(after) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 정보 부재", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<MemberSearchResponse>> searchUsers(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "닉네임 prefix 검색어 (2~100자)")
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "페이지 크기")
            @RequestParam(value = "size", required = false) String size,
            @Parameter(description = "커서 (createdAt|id 형식)")
            @RequestParam(value = "after", required = false) String after
    );

    @Operation(summary = "게시글 검색", description = "로그인 사용자 전용 게시글 검색 API입니다. query가 '#'으로 시작하면 태그 prefix 검색, 아니면 본문 prefix 검색을 수행합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = PostSearchResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 필수 입력", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 길이 제한", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "커서(after) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 정보 부재", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostSearchResponse>> searchPosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "본문 또는 태그 prefix 검색어 (2~100자)")
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "페이지 크기")
            @RequestParam(value = "size", required = false) String size,
            @Parameter(description = "커서 (createdAt|id 형식)")
            @RequestParam(value = "after", required = false) String after
    );
}
