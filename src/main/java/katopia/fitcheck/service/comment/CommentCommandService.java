package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.dto.comment.request.CommentRequest;
import katopia.fitcheck.dto.comment.response.CommentResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.service.post.PostFinder;
import katopia.fitcheck.service.notification.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final MemberFinder memberFinder;
    private final CommentValidator commentValidator;
    private final CommentFinder commentFinder;
    private final PostFinder postFinder;
    private final NotificationCommandService notificationService;
    private final CommentCountDeltaService commentCountDeltaService;

    @Transactional
    public CommentResponse create(Long memberId, Long postId, CommentRequest request) {
        Post post = postFinder.getReferenceById(postId);
        Member member = memberFinder.getReferenceById(memberId);

        Comment comment = Comment.create(post, member, request.content());
        try {
            Comment saved = commentRepository.save(comment);
            try {
                commentCountDeltaService.increase(postId);
            } catch (DataAccessException ex) {
                log.debug("Failed to increment comment delta. postId={}", postId, ex);
            }
            notificationService.publishPostCommentNotification(memberId, postId);
            return CommentResponse.of(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(CommonErrorCode.INVALID_RELATION);
        }
    }

    @Transactional
    public CommentResponse update(Long memberId, Long postId, Long commentId, CommentRequest request) {
        Comment comment = commentFinder.findByIdAndPostIdOrThrow(commentId, postId);
        commentValidator.validateOwner(comment, memberId);

        comment.updateContent(request.content());
        return CommentResponse.of(comment);
    }

    @Transactional
    public void delete(Long memberId, Long postId, Long commentId) {
        Comment comment = commentFinder.findByIdAndPostIdOrThrow(commentId, postId);
        commentValidator.validateOwner(comment, memberId);
        comment.markDeleted();
        try {
            commentCountDeltaService.decrease(postId);
        } catch (DataAccessException ex) {
            log.debug("Failed to decrement comment delta. postId={}", postId, ex);
        }
    }
}
