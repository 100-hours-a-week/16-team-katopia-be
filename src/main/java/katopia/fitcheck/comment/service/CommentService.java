package katopia.fitcheck.comment.service;

import katopia.fitcheck.comment.dto.CommentCreateRequest;
import katopia.fitcheck.comment.dto.CommentCreateResponse;
import katopia.fitcheck.comment.dto.CommentListResponse;
import katopia.fitcheck.comment.dto.CommentUpdateRequest;
import katopia.fitcheck.comment.dto.CommentUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    public CommentCreateResponse create(Long memberId, Long postId, CommentCreateRequest request) {
        return commentCommandService.create(memberId, postId, request);
    }

    public CommentListResponse list(Long postId, String sizeValue, String after) {
        return commentQueryService.list(postId, sizeValue, after);
    }

    public CommentUpdateResponse update(Long memberId, Long postId, Long commentId, CommentUpdateRequest request) {
        return commentCommandService.update(memberId, postId, commentId, request);
    }

    public void delete(Long memberId, Long postId, Long commentId) {
        commentCommandService.delete(memberId, postId, commentId);
    }
}
