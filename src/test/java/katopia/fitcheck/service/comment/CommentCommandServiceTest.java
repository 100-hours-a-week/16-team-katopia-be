package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.dto.comment.request.CommentRequest;
import katopia.fitcheck.dto.comment.response.CommentResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.post.PostFinder;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentCommandServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberFinder memberFinder;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentFinder commentFinder;

    @Mock
    private PostFinder postFinder;

    @InjectMocks
    private CommentCommandService commentCommandService;

    @Test
    @DisplayName("TC-COMMENT-CMD-06 댓글 생성 성공(연관 엔티티/카운트 증가)")
    void tcCommentCmd06_createComment_incrementsCount() {
        Member author = MemberTestFactory.member(1L);
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", 10L);

        Member commenter = MemberTestFactory.member(2L);

        doNothing().when(postFinder).requireExists(10L);
        when(postFinder.getReferenceById(10L)).thenReturn(post);
        when(memberFinder.getReferenceById(2L)).thenReturn(commenter);

        Comment saved = Comment.create(post, commenter, "hi");
        ReflectionTestUtils.setField(saved, "id", 100L);
        when(commentRepository.save(any())).thenReturn(saved);

        CommentResponse response = commentCommandService.create(2L, 10L, new CommentRequest("hi"));

        assertThat(response.content()).isEqualTo("hi");
        verify(commentRepository).save(any());
        verify(postRepository).incrementCommentCount(eq(10L));
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-01 댓글 작성 실패(게시글 없음)")
    void tcCommentCmd01_createFailsWhenPostMissing() {
        doThrow(new BusinessException(PostErrorCode.POST_NOT_FOUND)).when(postFinder).requireExists(10L);

        assertThatThrownBy(() -> commentCommandService.create(1L, 10L, new CommentRequest("hi")))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-02 댓글 수정 실패(댓글 없음)")
    void tcCommentCmd02_updateFailsWhenCommentMissing() {
        doNothing().when(postFinder).requireExists(10L);
        doThrow(new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND))
                .when(commentFinder)
                .findByIdAndPostIdOrThrow(100L, 10L);

        assertThatThrownBy(() -> commentCommandService.update(1L, 10L, 100L, new CommentRequest("hi")))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommentErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-03 댓글 수정 실패(작성자 아님)")
    void tcCommentCmd03_updateFailsWhenNotOwner() {
        doNothing().when(postFinder).requireExists(10L);
        Comment comment = buildComment(1L, 10L, "hi");
        when(commentFinder.findByIdAndPostIdOrThrow(100L, 10L)).thenReturn(comment);
        doThrow(new AuthException(AuthErrorCode.ACCESS_DENIED))
                .when(commentValidator)
                .validateOwner(eq(comment), eq(2L));

        assertThatThrownBy(() -> commentCommandService.update(2L, 10L, 100L, new CommentRequest("hi")))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-04 댓글 삭제 실패(작성자 아님)")
    void tcCommentCmd04_deleteFailsWhenNotOwner() {
        doNothing().when(postFinder).requireExists(10L);
        Comment comment = buildComment(1L, 10L, "hi");
        when(commentFinder.findByIdAndPostIdOrThrow(100L, 10L)).thenReturn(comment);
        doThrow(new AuthException(AuthErrorCode.ACCESS_DENIED))
                .when(commentValidator)
                .validateOwner(eq(comment), eq(2L));

        assertThatThrownBy(() -> commentCommandService.delete(2L, 10L, 100L))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-05 댓글 삭제 실패(댓글 없음)")
    void tcCommentCmd05_deleteFailsWhenCommentMissing() {
        doNothing().when(postFinder).requireExists(10L);
        doThrow(new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND))
                .when(commentFinder)
                .findByIdAndPostIdOrThrow(100L, 10L);

        assertThatThrownBy(() -> commentCommandService.delete(1L, 10L, 100L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommentErrorCode.COMMENT_NOT_FOUND);
    }

    private Comment buildComment(Long memberId, Long postId, String content) {
        Member member = MemberTestFactory.member(memberId);
        Post post = Post.create(member, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", postId);
        return Comment.create(post, member, content);
    }
}
