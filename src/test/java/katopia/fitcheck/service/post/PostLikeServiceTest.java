package katopia.fitcheck.service.post;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.domain.post.PostLike;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.PostLikeErrorCode;
import katopia.fitcheck.repository.post.PostLikeRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberFinder memberFinder;

    @Mock
    private PostFinder postFinder;

    @InjectMocks
    private PostLikeService postLikeService;

    @Test
    @DisplayName("TC-POST-LIKE-S-01 좋아요 성공(연관 엔티티/카운트 증가)")
    void tcPostLikeS01_likeSuccess_incrementsCount() {
        Member member = MemberTestFactory.member(1L);
        Post post = Post.create(member, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", 10L);

        when(postLikeRepository.existsByMemberIdAndPostId(eq(1L), eq(10L))).thenReturn(false);
        when(postFinder.getReferenceById(10L)).thenReturn(post);
        when(memberFinder.findByIdOrThrow(1L)).thenReturn(member);
        when(postFinder.findByIdOrThrow(10L)).thenReturn(post);

        postLikeService.like(1L, 10L);
    }

    @Test
    @DisplayName("TC-POST-LIKE-F-01 좋아요 실패(중복)")
    void tcPostLikeF01_alreadyLiked_throws() {
        when(postLikeRepository.existsByMemberIdAndPostId(eq(1L), eq(10L))).thenReturn(true);

        assertThatThrownBy(() -> postLikeService.like(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(PostLikeErrorCode.ALREADY_LIKED);
    }

    @Test
    @DisplayName("TC-POST-LIKE-F-02 좋아요 해제 실패(기록 없음)")
    void tcPostLikeF02_notFoundLike_throws() {
        when(postLikeRepository.findByMemberIdAndPostId(eq(1L), eq(10L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postLikeService.unlike(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(PostLikeErrorCode.NOT_FOUND_LIKE);
    }
}
