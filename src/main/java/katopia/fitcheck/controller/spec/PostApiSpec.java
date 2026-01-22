package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.post.dto.PostCreateRequest;
import katopia.fitcheck.post.dto.PostCreateResponse;
import katopia.fitcheck.post.dto.PostDetailResponse;
import katopia.fitcheck.post.dto.PostLikeResponse;
import katopia.fitcheck.post.dto.PostListResponse;
import katopia.fitcheck.post.dto.PostUpdateRequest;
import katopia.fitcheck.post.dto.PostUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface PostApiSpec {

    @Operation(summary = "게시글 작성", description = "게시글 작성 API 입니다.")
    @ApiResponse(responseCode = "201", description = "게시글 작성 성공", content = @Content(schema = @Schema(implementation = PostCreateResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 필수 입력 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 200자 초과", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "이미지 수량 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "태그 길이 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostCreateResponse>> createPost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody PostCreateRequest request
    );

    @Operation(summary = "게시글 목록 조회", description = "커서 기반 인피니티 스크롤을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공", content = @Content(schema = @Schema(implementation = PostListResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostListResponse>> listPosts(
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    );

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 보기 API 입니다.")
    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공", content = @Content(schema = @Schema(implementation = PostDetailResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostDetailResponse>> getPost(
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 수정", description = "게시글 수정 API 입니다.")
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공", content = @Content(schema = @Schema(implementation = PostUpdateResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 필수 입력 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 200자 초과", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "이미지 수량 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "태그 길이 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "403", description = "다른 사용자 게시글 수정 시도", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostUpdateResponse>> updatePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id,
            @RequestBody PostUpdateRequest request
    );

    @Operation(summary = "게시글 삭제", description = "게시글 삭제 API 입니다.")
    @ApiResponse(responseCode = "204", description = "게시글 삭제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "403", description = "다른 사용자 게시글 삭제 시도", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요 API 입니다.")
    @ApiResponse(responseCode = "201", description = "게시글 좋아요 성공", content = @Content(schema = @Schema(implementation = PostLikeResponse.class)))
    @ApiResponse(responseCode = "400", description = "식별자 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "409", description = "이미 좋아요를 누른 게시글", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<PostLikeResponse>> likePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 좋아요 해제", description = "게시글 좋아요 해제 API 입니다.")
    @ApiResponse(responseCode = "204", description = "게시글 좋아요 해제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "식별자 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "좋아요 기록 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );
}
