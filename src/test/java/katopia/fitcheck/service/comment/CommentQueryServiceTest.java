package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.dto.comment.response.CommentListResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.service.post.PostFinder;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentQueryServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostFinder postFinder;

    @InjectMocks
    private CommentQueryService commentQueryService;

    @Test
    @DisplayName("TC-COMMENT-QUERY-S-01 댓글 목록: 최신 목록과 다음 커서 생성")
    void list_returnsNextCursorWhenPageFull() {
        Post post = buildPost(10L);
        Comment first = buildComment(post, 1L, LocalDateTime.of(2026, 2, 1, 10, 0));
        Comment last = buildComment(post, 2L, LocalDateTime.of(2026, 2, 1, 9, 0));
        doNothing().when(postFinder).requireExists(10L);
        when(commentRepository.findLatestByPostId(eq(10L), eq(PageRequest.of(0, 2))))
                .thenReturn(List.of(first, last));

        CommentListResponse response = commentQueryService.list(10L, "2", null);

        assertThat(response.comments()).hasSize(2);
        String expected = CursorPagingHelper.encodeCursor(last.getCreatedAt(), last.getId());
        assertThat(response.nextCursor()).isEqualTo(expected);
    }

    @Test
    @DisplayName("TC-COMMENT-QUERY-S-02 댓글 목록: 커서 이후 페이지 조회")
    void list_usesAfterCursor() {
        Post post = buildPost(10L);
        Comment last = buildComment(post, 3L, LocalDateTime.of(2026, 2, 1, 8, 0));
        doNothing().when(postFinder).requireExists(10L);
        String after = CursorPagingHelper.encodeCursor(
                LocalDateTime.of(2026, 2, 1, 9, 0), 2L
        );
        when(commentRepository.findPageAfter(
                eq(10L),
                eq(LocalDateTime.of(2026, 2, 1, 9, 0)),
                eq(2L),
                eq(PageRequest.of(0, 1))
        )).thenReturn(List.of(last));

        CommentListResponse response = commentQueryService.list(10L, "1", after);

        assertThat(response.comments()).hasSize(1);
        assertThat(response.nextCursor()).isEqualTo(CursorPagingHelper.encodeCursor(last.getCreatedAt(), last.getId()));
    }

    @Test
    @DisplayName("TC-COMMENT-QUERY-F-01 댓글 목록 실패(게시글 없음)")
    void list_throwsWhenPostMissing() {
        doThrow(new BusinessException(PostErrorCode.POST_NOT_FOUND))
                .when(postFinder)
                .requireExists(10L);

        assertThatThrownBy(() -> commentQueryService.list(10L, "1", null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }

    private Post buildPost(Long postId) {
        Member member = MemberTestFactory.member(1L);
        Post post = Post.create(member, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", postId);
        return post;
    }

    private Comment buildComment(Post post, Long id, LocalDateTime createdAt) {
        Member author = MemberTestFactory.member(id);
        Comment comment = Comment.create(post, author, "content");
        ReflectionTestUtils.setField(comment, "id", id);
        ReflectionTestUtils.setField(comment, "createdAt", createdAt);
        return comment;
    }
}
