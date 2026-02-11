package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import katopia.fitcheck.dto.comment.request.CommentRequest;
import katopia.fitcheck.dto.comment.response.CommentResponse;
import katopia.fitcheck.dto.comment.response.CommentListResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface CommentApiSpec {

    @Operation(summary = "댓글 작성", description = "댓글 작성 API 입니다.")
    @ApiResponse(responseCode = "201", description = "댓글 작성 성공", content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<CommentResponse>> createComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentRequest request
    );

    @Operation(summary = "댓글 목록 조회", description = Docs.CURSOR_PAGING_DES)
    @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공", content = @Content(schema = @Schema(implementation = CommentListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<CommentListResponse>> listComments(
            @PathVariable("postId") Long postId,
            @Parameter(description = Docs.PAGE_DES)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "댓글 수정", description = "댓글 수정 API 입니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공", content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<CommentResponse>> updateComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @PathVariable("id") Long commentId,
            @Valid @RequestBody CommentRequest request
    );

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 API 입니다.")
    @ApiResponse(responseCode = "204", description = "댓글 삭제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @PathVariable("id") Long commentId
    );
}
