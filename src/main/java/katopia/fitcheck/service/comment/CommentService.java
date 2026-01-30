package katopia.fitcheck.service.comment;

import katopia.fitcheck.dto.comment.CommentCreateRequest;
import katopia.fitcheck.dto.comment.CommentCreateResponse;
import katopia.fitcheck.dto.comment.CommentListResponse;
import katopia.fitcheck.dto.comment.CommentUpdateRequest;
import katopia.fitcheck.dto.comment.CommentUpdateResponse;
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
