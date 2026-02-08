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
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.service.post.PostFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberFinder memberFinder;
    private final CommentValidator commentValidator;
    private final CommentFinder commentFinder;
    private final PostFinder postFinder;

    @Transactional
    public CommentResponse create(Long memberId, Long postId, CommentRequest request) {
        // postFinder.requireExists(postId);
        Post post = postFinder.getReferenceById(postId);
        Member member = memberFinder.getReferenceById(memberId);

        Comment comment = Comment.create(post, member, request.content());
        try {
            Comment saved = commentRepository.save(comment);
            postRepository.incrementCommentCount(postId);
            return CommentResponse.of(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(CommonErrorCode.INVALID_RELATION);
        }
    }

    @Transactional
    public CommentResponse update(Long memberId, Long postId, Long commentId, CommentRequest request) {
        postFinder.requireExists(postId);
        Comment comment = commentFinder.findByIdAndPostIdOrThrow(commentId, postId);
        commentValidator.validateOwner(comment, memberId);

        comment.updateContent(request.content());
        return CommentResponse.of(comment);
    }

    @Transactional
    public void delete(Long memberId, Long postId, Long commentId) {
        postFinder.requireExists(postId);
        Comment comment = commentFinder.findByIdAndPostIdOrThrow(commentId, postId);
        commentValidator.validateOwner(comment, memberId);
        commentRepository.delete(comment);
        postRepository.decrementCommentCount(postId);
    }
}
