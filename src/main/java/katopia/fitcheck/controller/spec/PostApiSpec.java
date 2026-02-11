package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.dto.post.request.PostCreateRequest;
import katopia.fitcheck.dto.post.response.PostCreateResponse;
import katopia.fitcheck.dto.post.response.PostBookmarkResponse;
import katopia.fitcheck.dto.post.response.PostDetailResponse;
import katopia.fitcheck.dto.post.response.PostLikeResponse;
import katopia.fitcheck.dto.post.response.PostListResponse;
import katopia.fitcheck.dto.post.request.PostUpdateRequest;
import katopia.fitcheck.dto.post.response.PostUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface PostApiSpec {

    @Operation(summary = "게시글 작성", description = "게시글 작성 API 입니다.")
    @ApiResponse(responseCode = "201", description = "게시글 작성 성공", content = @Content(schema = @Schema(implementation = PostCreateResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<PostCreateResponse>> createPost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody PostCreateRequest request
    );

    @Operation(summary = "게시글 목록 조회", description = "커서 기반 인피니티 스크롤을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공", content = @Content(schema = @Schema(implementation = PostListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    ResponseEntity<APIResponse<PostListResponse>> listPosts(
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 보기 API 입니다.")
    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공", content = @Content(schema = @Schema(implementation = PostDetailResponse.class)))
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<PostDetailResponse>> getPost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 수정", description = "게시글 수정 API 입니다.")
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공", content = @Content(schema = @Schema(implementation = PostUpdateResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<PostUpdateResponse>> updatePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id,
            @Valid @RequestBody PostUpdateRequest request
    );

    @Operation(summary = "게시글 삭제", description = "게시글 삭제 API 입니다.")
    @ApiResponse(responseCode = "204", description = "게시글 삭제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요 API 입니다.")
    @ApiResponse(responseCode = "201", description = "게시글 좋아요 성공", content = @Content(schema = @Schema(implementation = PostLikeResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @ApiResponse(responseCode = "409", description = "이미 좋아요를 누른 게시글", content = @Content)
    ResponseEntity<APIResponse<PostLikeResponse>> likePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 좋아요 해제", description = "게시글 좋아요 해제 API 입니다.")
    @ApiResponse(responseCode = "204", description = "게시글 좋아요 해제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 북마크", description = "게시글 북마크 API 입니다.")
    @ApiResponse(responseCode = "201", description = "게시글 북마크 성공", content = @Content(schema = @Schema(implementation = PostBookmarkResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @ApiResponse(responseCode = "409", description = "이미 북마크한 게시글", content = @Content)
    ResponseEntity<APIResponse<PostBookmarkResponse>> bookmarkPost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );

    @Operation(summary = "게시글 북마크 해제", description = "게시글 북마크 해제 API 입니다.")
    @ApiResponse(responseCode = "204", description = "게시글 북마크 해제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<Void> unbookmarkPost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    );
}
