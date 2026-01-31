package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFinder {

    private final CommentRepository commentRepository;

    public Comment findByIdAndPostIdOrThrow(Long commentId, Long postId) {
        return commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
    }
}
