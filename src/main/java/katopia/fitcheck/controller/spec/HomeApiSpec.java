package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.dto.post.response.PostResponse;
import katopia.fitcheck.dto.recommendation.RecommendationResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

public interface HomeApiSpec {

    @Operation(summary = "홈 피드 게시글 목록", description = "팔로우한 사용자 + 내 게시글을 최신순으로 제공합니다.")
    @ApiResponse(responseCode = "200", description = "피드 조회 성공", content = @Content(schema = @Schema(implementation = PostResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostResponse>> listHomePosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "페이지 크기", example = SwaggerExamples.PAGE_SIZE_EXAMPLE)
            @RequestParam(value = "size", required = false) String size,
            @Parameter(description = "커서 (createdAt|id 형식)", example = SwaggerExamples.TIMESTAMP_EXAMPLE + "|1")
            @RequestParam(value = "after", required = false) String after
    );

    @Operation(summary = "홈 추천 친구 목록", description = "팔로우-팔로우 기반 추천 + 없으면 최신 가입자")
    @ApiResponse(responseCode = "200", description = "추천 조회 성공", content = @Content(schema = @Schema(implementation = RecommendationResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<RecommendationResponse>> listHomeMembers(
            @AuthenticationPrincipal MemberPrincipal principal
    );
}
