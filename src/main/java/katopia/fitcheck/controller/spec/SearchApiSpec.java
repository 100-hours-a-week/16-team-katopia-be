package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.dto.search.PostSearchResponse;
import katopia.fitcheck.dto.search.MemberSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

public interface SearchApiSpec {

    @Operation(summary = "계정 검색", description = "닉네임 prefix 검색을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = MemberSearchResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<MemberSearchResponse>> searchUsers(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "닉네임 prefix 검색어 (2~100자)")
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "게시글 본문/태그 검색", description = "FULLTEXT 기반 본문 검색 또는 게시글 태그 prefix 검색을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = PostSearchResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<PostSearchResponse>> searchPosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "본문 | 태그 검색어")
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );
}
