package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.search.dto.PostSearchResponse;
import katopia.fitcheck.search.dto.MemberSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

public interface SearchApiSpec {

    @Operation(summary = "계정 검색", description = "계정 검색 API입니다. query 기반 닉네임 prefix 검색과 커서 기반 인피니티 스크롤을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = MemberSearchResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 필수 입력", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 길이 제한", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "커서(after) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "성별 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "키/몸무게 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<MemberSearchResponse>> searchUsers(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "닉네임 prefix 검색어 (2~100자)")
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "페이지 크기")
            @RequestParam(value = "size", required = false) String size,
            @Parameter(description = "커서 (createdAt|id 형식)")
            @RequestParam(value = "after", required = false) String after,
            @Parameter(description = "키(cm) 필터 - 입력 시 ±3 범위 적용")
            @RequestParam(value = "height", required = false) String height,
            @Parameter(description = "몸무게(kg) 필터 - 입력 시 ±3 범위 적용")
            @RequestParam(value = "weight", required = false) String weight,
            @Parameter(description = "성별 필터 (M/F)")
            @RequestParam(value = "gender", required = false) String gender
    );

    @Operation(summary = "게시글 검색", description = "게시글 내용/태그 OR 검색 API입니다. query 기반 prefix 검색과 커서 기반 인피니티 스크롤을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = PostSearchResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 필수 입력", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "검색어 길이 제한", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "커서(after) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "성별 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "키/몸무게 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostSearchResponse>> searchPosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "본문 또는 태그 prefix 검색어 (2~100자)")
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "페이지 크기")
            @RequestParam(value = "size", required = false) String size,
            @Parameter(description = "커서 (createdAt|id 형식)")
            @RequestParam(value = "after", required = false) String after,
            @Parameter(description = "작성자 키(cm) 필터 - 입력 시 ±3 범위 적용")
            @RequestParam(value = "height", required = false) String height,
            @Parameter(description = "작성자 몸무게(kg) 필터 - 입력 시 ±3 범위 적용")
            @RequestParam(value = "weight", required = false) String weight,
            @Parameter(description = "작성자 성별 필터 (M/F)")
            @RequestParam(value = "gender", required = false) String gender
    );
}
