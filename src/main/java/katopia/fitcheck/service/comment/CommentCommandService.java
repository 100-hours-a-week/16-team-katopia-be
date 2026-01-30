package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.dto.comment.request.CommentCreateRequest;
import katopia.fitcheck.dto.comment.response.CommentCreateResponse;
import katopia.fitcheck.dto.comment.request.CommentUpdateRequest;
import katopia.fitcheck.dto.comment.response.CommentUpdateResponse;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.service.post.PostFinder;
import lombok.RequiredArgsConstructor;
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
    public CommentCreateResponse create(Long memberId, Long postId, CommentCreateRequest request) {
        postFinder.requireExists(postId);
        Post post = postFinder.getReferenceById(postId);
        Member member = memberFinder.getReferenceById(memberId);
        String content = commentValidator.validateContent(request.content());

        Comment comment = Comment.create(post, member, content);
        Comment saved = commentRepository.save(comment);
        postRepository.incrementCommentCount(postId);
        return CommentCreateResponse.of(saved);
    }

    @Transactional
    public CommentUpdateResponse update(Long memberId, Long postId, Long commentId, CommentUpdateRequest request) {
        postFinder.requireExists(postId);
        Comment comment = commentFinder.findByIdAndPostIdOrThrow(commentId, postId);
        commentValidator.validateOwner(comment, memberId);

        String content = commentValidator.validateContent(request.content());
        comment.updateContent(content);
        return CommentUpdateResponse.of(comment);
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
