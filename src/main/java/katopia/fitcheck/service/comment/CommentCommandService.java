package katopia.fitcheck.service.comment;

import java.time.LocalDateTime;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final MemberFinder memberFinder;
    private final CommentValidator commentValidator;
    private final CommentFinder commentFinder;
    private final PostFinder postFinder;
    private final NotificationCommandService notificationService;

    @Transactional
    public CommentResponse create(Long memberId, Long postId, CommentRequest request) {
        Post post = postFinder.getReferenceById(postId);
        Member member = memberFinder.getReferenceById(memberId);

        Comment comment = Comment.create(post, member, request.content());
        try {
            Comment saved = commentRepository.save(comment);
            // TODO: 댓글 집계는 Redis 기준으로 처리 후 비동기 DB 동기화로 전환
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
    }
}
