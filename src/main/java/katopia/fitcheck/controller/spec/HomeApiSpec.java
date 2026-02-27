package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.dto.post.response.PostResponse;
import katopia.fitcheck.dto.recommendation.RecommendationResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

public interface HomeApiSpec {

    @Operation(summary = "홈 피드 게시글 목록", description = "팔로우한 사용자 + 내 게시글을 최신순으로 제공합니다.")
    @ApiResponse(responseCode = "200", description = "피드 조회 성공", content = @Content(schema = @Schema(implementation = PostResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<PostResponse>> listHomePosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "홈 추천 친구 목록", description = "팔로우-팔로우 기반 추천 + 없으면 최신 가입자")
    @ApiResponse(responseCode = "200", description = "추천 조회 성공", content = @Content(schema = @Schema(implementation = RecommendationResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<RecommendationResponse>> listHomeMembers(
            @AuthenticationPrincipal MemberPrincipal principal
    );
}
