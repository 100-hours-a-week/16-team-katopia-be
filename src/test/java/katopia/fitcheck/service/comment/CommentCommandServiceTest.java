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
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.post.PostFinder;
import katopia.fitcheck.service.notification.NotificationService;
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
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class CommentCommandServiceTest {

    private static final Long AUTHOR_ID = 1L;
    private static final Long COMMENTER_ID = 2L;
    private static final Long POST_ID = 10L;
    private static final Long COMMENT_ID = 100L;

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

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentCommandService commentCommandService;

    @Test
    @DisplayName("TC-COMMENT-CMD-S-01 댓글 생성 성공(연관 엔티티/카운트 증가)")
    void tcCommentCmdS01_createComment_incrementsCount() {
        Member author = MemberTestFactory.member(AUTHOR_ID);
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", POST_ID);

        Member commenter = MemberTestFactory.member(COMMENTER_ID);

        when(postFinder.getReferenceById(POST_ID)).thenReturn(post);
        when(memberFinder.getReferenceById(COMMENTER_ID)).thenReturn(commenter);

        Comment saved = Comment.create(post, commenter, "hi");
        ReflectionTestUtils.setField(saved, "id", COMMENT_ID);
        when(commentRepository.save(any())).thenReturn(saved);

        CommentResponse response = commentCommandService.create(COMMENTER_ID, POST_ID, new CommentRequest("hi"));

        assertThat(response.content()).isEqualTo("hi");
        verify(commentRepository).save(any());
        verify(postRepository).incrementCommentCount(eq(POST_ID));
        verify(notificationService).createPostComment(eq(COMMENTER_ID), eq(POST_ID));
    }

    @Test
    @DisplayName("TC-TRIGGER-S-04 댓글 생성 시 알림 트리거")
    void tcTriggerS04_comment_triggersNotification() {
        Member author = MemberTestFactory.member(AUTHOR_ID);
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", POST_ID);

        Member commenter = MemberTestFactory.member(COMMENTER_ID);

        when(postFinder.getReferenceById(POST_ID)).thenReturn(post);
        when(memberFinder.getReferenceById(COMMENTER_ID)).thenReturn(commenter);

        Comment saved = Comment.create(post, commenter, "hi");
        ReflectionTestUtils.setField(saved, "id", COMMENT_ID);
        when(commentRepository.save(any())).thenReturn(saved);

        commentCommandService.create(COMMENTER_ID, POST_ID, new CommentRequest("hi"));

        verify(notificationService).createPostComment(eq(COMMENTER_ID), eq(POST_ID));
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-S-02 댓글 수정 성공(본문 변경)")
    void tcCommentCmdS02_updateComment_updatesContent() {
        doNothing().when(postFinder).requireExists(POST_ID);
        Comment comment = buildComment(AUTHOR_ID, POST_ID, "before");
        when(commentFinder.findByIdAndPostIdOrThrow(COMMENT_ID, POST_ID)).thenReturn(comment);
        doNothing().when(commentValidator).validateOwner(eq(comment), eq(AUTHOR_ID));

        CommentResponse response = commentCommandService.update(AUTHOR_ID, POST_ID, COMMENT_ID, new CommentRequest("after"));

        assertThat(response.content()).isEqualTo("after");
        assertThat(comment.getContent()).isEqualTo("after");
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-F-01 댓글 작성 실패(연관관계 오류)")
    void tcCommentCmdF01_createFailsWhenRelationInvalid() {
        Member author = MemberTestFactory.member(AUTHOR_ID);
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", POST_ID);
        when(postFinder.getReferenceById(POST_ID)).thenReturn(post);
        when(memberFinder.getReferenceById(AUTHOR_ID)).thenReturn(author);
        when(commentRepository.save(any())).thenThrow(new DataIntegrityViolationException("fk"));

        assertThatThrownBy(() -> commentCommandService.create(AUTHOR_ID, POST_ID, new CommentRequest("hi")))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_RELATION);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-F-02 댓글 수정 실패(댓글 없음)")
    void tcCommentCmdF02_updateFailsWhenCommentMissing() {
        doNothing().when(postFinder).requireExists(POST_ID);
        doThrow(new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND))
                .when(commentFinder)
                .findByIdAndPostIdOrThrow(COMMENT_ID, POST_ID);

        assertThatThrownBy(() -> commentCommandService.update(AUTHOR_ID, POST_ID, COMMENT_ID, new CommentRequest("hi")))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommentErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-F-03 댓글 수정 실패(작성자 아님)")
    void tcCommentCmdF03_updateFailsWhenNotOwner() {
        doNothing().when(postFinder).requireExists(POST_ID);
        Comment comment = buildComment(AUTHOR_ID, POST_ID, "hi");
        when(commentFinder.findByIdAndPostIdOrThrow(COMMENT_ID, POST_ID)).thenReturn(comment);
        doThrow(new AuthException(AuthErrorCode.ACCESS_DENIED))
                .when(commentValidator)
                .validateOwner(eq(comment), eq(COMMENTER_ID));

        assertThatThrownBy(() -> commentCommandService.update(COMMENTER_ID, POST_ID, COMMENT_ID, new CommentRequest("hi")))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-F-04 댓글 삭제 실패(작성자 아님)")
    void tcCommentCmdF04_deleteFailsWhenNotOwner() {
        doNothing().when(postFinder).requireExists(POST_ID);
        Comment comment = buildComment(AUTHOR_ID, POST_ID, "hi");
        when(commentFinder.findByIdAndPostIdOrThrow(COMMENT_ID, POST_ID)).thenReturn(comment);
        doThrow(new AuthException(AuthErrorCode.ACCESS_DENIED))
                .when(commentValidator)
                .validateOwner(eq(comment), eq(COMMENTER_ID));

        assertThatThrownBy(() -> commentCommandService.delete(COMMENTER_ID, POST_ID, COMMENT_ID))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("TC-COMMENT-CMD-F-05 댓글 삭제 실패(댓글 없음)")
    void tcCommentCmdF05_deleteFailsWhenCommentMissing() {
        doNothing().when(postFinder).requireExists(POST_ID);
        doThrow(new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND))
                .when(commentFinder)
                .findByIdAndPostIdOrThrow(COMMENT_ID, POST_ID);

        assertThatThrownBy(() -> commentCommandService.delete(AUTHOR_ID, POST_ID, COMMENT_ID))
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
