package katopia.fitcheck.service.comment;

import katopia.fitcheck.dto.comment.request.CommentRequest;
import katopia.fitcheck.dto.comment.response.CommentResponse;
import katopia.fitcheck.dto.comment.response.CommentListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    public CommentResponse create(Long memberId, Long postId, CommentRequest request) {
        return commentCommandService.create(memberId, postId, request);
    }

    public CommentListResponse list(Long postId, String sizeValue, String after) {
        return commentQueryService.list(postId, sizeValue, after);
    }

    public CommentResponse update(Long memberId, Long postId, Long commentId, CommentRequest request) {
        return commentCommandService.update(memberId, postId, commentId, request);
    }

    public void delete(Long memberId, Long postId, Long commentId) {
        commentCommandService.delete(memberId, postId, commentId);
    }
}
