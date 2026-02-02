package katopia.fitcheck.service.post;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.dto.post.response.PostDetailResponse;
import katopia.fitcheck.dto.post.response.PostListResponse;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.repository.post.PostLikeRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.repository.post.PostTagRepository;
import katopia.fitcheck.service.member.MemberFinder;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostSearchServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFinder postFinder;

    @Mock
    private MemberFinder memberFinder;

    @Mock
    private PostTagRepository postTagRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostSearchService postSearchService;

    @Test
    @DisplayName("게시글 목록: 최신 목록과 다음 커서 생성")
    void list_returnsNextCursorWhenPageFull() {
        Post first = buildPost(1L, LocalDateTime.of(2026, 2, 1, 10, 0), "img1");
        Post last = buildPost(2L, LocalDateTime.of(2026, 2, 1, 9, 0), "img2");
        when(postRepository.findLatest(eq(PageRequest.of(0, 2)))).thenReturn(List.of(first, last));

        PostListResponse response = postSearchService.list("2", null);

        assertThat(response.posts()).hasSize(2);
        assertThat(response.nextCursor()).isNotNull();
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(response.nextCursor());
        assertThat(cursor.id()).isEqualTo(2L);
        assertThat(cursor.createdAt()).isEqualTo(LocalDateTime.of(2026, 2, 1, 9, 0));
    }

    @Test
    @DisplayName("회원별 게시글 목록: 멤버 검증 후 목록 조회")
    void listByMember_validatesMemberAndLoadsPosts() {
        Post post = buildPost(1L, LocalDateTime.of(2026, 2, 1, 10, 0), "img1");
        when(postRepository.findLatestByMemberId(eq(1L), eq(PageRequest.of(0, 1)))).thenReturn(List.of(post));

        PostListResponse response = postSearchService.listByMember(1L, "1", null);

        verify(memberFinder).findActiveByIdOrThrow(1L);
        assertThat(response.posts()).hasSize(1);
        assertThat(response.nextCursor()).isNotNull();
    }

    @Test
    @DisplayName("게시글 상세: 태그와 좋아요 여부 포함")
    void getDetail_returnsTagsAndLikeState() {
        Member author = Member.builder()
                .id(7L)
                .nickname("author")
                .oauth2Provider(SocialProvider.KAKAO)
                .oauth2UserId("oauth")
                .build();
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img1")));
        ReflectionTestUtils.setField(post, "id", 10L);

        when(postFinder.findDetailByIdOrThrow(10L)).thenReturn(post);
        when(postTagRepository.findTagNamesByPostId(10L)).thenReturn(List.of("tag1", "tag2"));
        when(postLikeRepository.existsByMemberIdAndPostId(1L, 10L)).thenReturn(true);

        PostDetailResponse response = postSearchService.getDetail(1L, 10L);

        assertThat(response.tags()).containsExactly("tag1", "tag2");
        assertThat(response.isLiked()).isTrue();
    }

    private Post buildPost(Long id, LocalDateTime createdAt, String imageKey) {
        Member member = Member.builder()
                .id(1L)
                .nickname("author")
                .oauth2Provider(SocialProvider.KAKAO)
                .oauth2UserId("oauth")
                .build();
        Post post = Post.create(member, "content", List.of(PostImage.of(1, imageKey)));
        ReflectionTestUtils.setField(post, "id", id);
        ReflectionTestUtils.setField(post, "createdAt", createdAt);
        return post;
    }
}
