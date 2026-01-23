package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.comment.dto.CommentCreateRequest;
import katopia.fitcheck.comment.dto.CommentCreateResponse;
import katopia.fitcheck.comment.dto.CommentListResponse;
import katopia.fitcheck.comment.dto.CommentUpdateRequest;
import katopia.fitcheck.comment.dto.CommentUpdateResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface CommentApiSpec {

    @Operation(summary = "댓글 작성", description = "댓글 작성 API 입니다.")
    @ApiResponse(responseCode = "201", description = "댓글 작성 성공", content = @Content(schema = @Schema(implementation = CommentCreateResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 필수 입력 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 200자 초과", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<CommentCreateResponse>> createComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @RequestBody CommentCreateRequest request
    );

    @Operation(summary = "댓글 목록 조회", description = "커서 기반 인피니티 스크롤을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공", content = @Content(schema = @Schema(implementation = CommentListResponse.class)))
    @ApiResponse(responseCode = "400", description = "페이지 크기(size) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "커서(after) 형식 오류", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<CommentListResponse>> listComments(
            @PathVariable("postId") Long postId,
            @Parameter(description = "페이지 크기")
            @RequestParam(value = "size", required = false) String size,
            @Parameter(description = "커서 (createdAt|id 형식)")
            @RequestParam(value = "after", required = false) String after
    );

    @Operation(summary = "댓글 수정", description = "댓글 수정 API 입니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공", content = @Content(schema = @Schema(implementation = CommentUpdateResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 필수 입력 검증 실패", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "400", description = "본문 200자 초과", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "403", description = "다른 사용자 댓글 수정 시도", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<APIResponse<CommentUpdateResponse>> updateComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @PathVariable("id") Long commentId,
            @RequestBody CommentUpdateRequest request
    );

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 API 입니다.")
    @ApiResponse(responseCode = "204", description = "댓글 삭제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 누락", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = "AT 만료/위조", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "403", description = "다른 사용자 댓글 삭제 시도", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @PathVariable("id") Long commentId
    );
}
