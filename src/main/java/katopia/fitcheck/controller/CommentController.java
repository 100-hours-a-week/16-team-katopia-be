package katopia.fitcheck.controller;

import katopia.fitcheck.dto.comment.CommentCreateRequest;
import katopia.fitcheck.dto.comment.CommentCreateResponse;
import katopia.fitcheck.dto.comment.CommentListResponse;
import katopia.fitcheck.dto.comment.CommentUpdateRequest;
import katopia.fitcheck.dto.comment.CommentUpdateResponse;
import katopia.fitcheck.service.comment.CommentService;
import katopia.fitcheck.controller.spec.CommentApiSpec;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.CommentSuccessCode;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController implements CommentApiSpec {

    private final CommentService commentService;
    private final SecuritySupport securitySupport;

    @PostMapping
    @Override
    public ResponseEntity<APIResponse<CommentCreateResponse>> createComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        CommentCreateResponse body = commentService.create(memberId, postId, request);

        return APIResponse.ok(CommentSuccessCode.COMMENT_CREATED, body);
    }

    @GetMapping
    @Override
    public ResponseEntity<APIResponse<CommentListResponse>> listComments(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        CommentListResponse body = commentService.list(postId, size, after);

        return APIResponse.ok(CommentSuccessCode.COMMENT_LISTED, body);
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<APIResponse<CommentUpdateResponse>> updateComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @PathVariable("id") Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        CommentUpdateResponse body = commentService.update(memberId, postId, commentId, request);

        return APIResponse.ok(CommentSuccessCode.COMMENT_UPDATED, body);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("postId") Long postId,
            @PathVariable("id") Long commentId
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        commentService.delete(memberId, postId, commentId);

        return APIResponse.noContent(CommentSuccessCode.COMMENT_DELETED);
    }
}
