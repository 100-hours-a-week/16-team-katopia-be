package katopia.fitcheck.service.post;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.PostLikeErrorCode;
import katopia.fitcheck.repository.post.PostLikeRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.notification.NotificationService;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    private static final Long MEMBER_ID = 1L;
    private static final Long AUTHOR_ID = 2L;
    private static final Long POST_ID = 10L;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberFinder memberFinder;

    @Mock
    private PostFinder postFinder;

    @Mock
    private NotificationService notificationService;

    private PostLikeService postLikeService;

    @BeforeEach
    void setUp() {
        postLikeService = new PostLikeService(
                postLikeRepository,
                postRepository,
                memberFinder,
                postFinder,
                notificationService
        );
    }

    @Test
    @DisplayName("TC-POST-LIKE-S-01 좋아요 성공(연관 엔티티/카운트 증가)")
    void tcPostLikeS01_likeSuccess_incrementsCount() {
        Member author = MemberTestFactory.member(AUTHOR_ID);
        Member member = MemberTestFactory.member(MEMBER_ID);
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", POST_ID);

        when(postLikeRepository.existsByMemberIdAndPostId(eq(MEMBER_ID), eq(POST_ID))).thenReturn(false);
        when(postFinder.getReferenceById(POST_ID)).thenReturn(post);
        when(memberFinder.findByIdOrThrow(MEMBER_ID)).thenReturn(member);
        when(postFinder.findByIdOrThrow(POST_ID)).thenReturn(post);

        postLikeService.like(MEMBER_ID, POST_ID);

        verify(notificationService).createPostLike(eq(member), eq(author), eq(POST_ID));
    }

    @Test
    @DisplayName("TC-TRIGGER-S-03 게시글 좋아요 시 알림 트리거")
    void tcTriggerS03_like_triggersNotification() {
        Member author = MemberTestFactory.member(AUTHOR_ID);
        Member member = MemberTestFactory.member(MEMBER_ID);
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", POST_ID);

        when(postLikeRepository.existsByMemberIdAndPostId(eq(MEMBER_ID), eq(POST_ID))).thenReturn(false);
        when(postFinder.getReferenceById(POST_ID)).thenReturn(post);
        when(memberFinder.findByIdOrThrow(MEMBER_ID)).thenReturn(member);
        when(postFinder.findByIdOrThrow(POST_ID)).thenReturn(post);

        postLikeService.like(MEMBER_ID, POST_ID);

        verify(notificationService).createPostLike(eq(member), eq(author), eq(POST_ID));
    }

    @Test
    @DisplayName("TC-POST-LIKE-F-01 좋아요 실패(중복)")
    void tcPostLikeF01_alreadyLiked_throws() {
        when(postLikeRepository.existsByMemberIdAndPostId(eq(MEMBER_ID), eq(POST_ID))).thenReturn(true);

        assertThatThrownBy(() -> postLikeService.like(MEMBER_ID, POST_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(PostLikeErrorCode.ALREADY_LIKED);
    }

    @Test
    @DisplayName("TC-POST-LIKE-F-02 좋아요 해제 실패(기록 없음)")
    void tcPostLikeF02_notFoundLike_throws() {
        when(postLikeRepository.findByMemberIdAndPostId(eq(MEMBER_ID), eq(POST_ID))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postLikeService.unlike(MEMBER_ID, POST_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(PostLikeErrorCode.NOT_FOUND_LIKE);
    }
}
