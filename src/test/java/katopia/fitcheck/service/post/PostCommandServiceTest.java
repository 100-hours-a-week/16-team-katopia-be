package katopia.fitcheck.service.post;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.domain.post.PostTag;
import katopia.fitcheck.domain.post.Tag;
import katopia.fitcheck.dto.post.request.PostCreateRequest;
import katopia.fitcheck.dto.post.request.PostUpdateRequest;
import katopia.fitcheck.dto.post.response.PostCreateResponse;
import katopia.fitcheck.dto.post.response.PostUpdateResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.repository.post.PostLikeRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.repository.post.PostTagRepository;
import katopia.fitcheck.repository.post.TagRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommandServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostTagRepository postTagRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private MemberFinder memberFinder;

    @Mock
    private PostFinder postFinder;

    @InjectMocks
    private PostCommandService postCommandService;

    @Test
    @DisplayName("TC-POST-CMD-01 게시글 생성: 본문/이미지/태그 정규화 후 저장")
    void create_normalizesContentImagesAndTags() {
        Member member = MemberTestFactory.member(1L);
        when(memberFinder.getReferenceById(1L)).thenReturn(member);
        Tag existing = Tag.of("Tag1");
        ReflectionTestUtils.setField(existing, "id", 1L);
        when(tagRepository.findByNameIn(eq(List.of("Tag1", "tag2")))).thenReturn(List.of(existing));

        Tag newTag = Tag.of("tag2");
        ReflectionTestUtils.setField(newTag, "id", 2L);
        when(tagRepository.saveAll(any())).thenReturn(List.of(newTag));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            ReflectionTestUtils.setField(post, "id", 10L);
            return post;
        });

        PostCreateRequest request = new PostCreateRequest(
                " hello ",
                List.of(" img1 ", "img2 "),
                List.of(" #Tag1 ", "tag2")
        );

        PostCreateResponse response = postCommandService.create(1L, request);

        Post saved = postCaptor.getValue();
        assertThat(saved.getContent()).isEqualTo("hello");
        assertThat(saved.getImages()).hasSize(2);
        assertThat(saved.getImages().getFirst().getImageObjectKey()).isEqualTo("img1");
        assertThat(saved.getPostTags()).hasSize(2);
        assertThat(response.content()).isEqualTo("hello");
        assertThat(response.imageObjectKeys()).hasSize(2);

        verify(memberRepository).incrementPostCount(1L);
        verify(tagRepository).saveAll(any());
    }

    @Test
    @DisplayName("TC-POST-CMD-02 게시글 수정: 본문 업데이트 및 태그 동기화")
    void update_updatesContentAndSyncsTags() {
        Member member = MemberTestFactory.member(1L);
        Post post = Post.create(member, "old", List.of(PostImage.of(1, "img")));

        Tag tag1 = Tag.of("tag1");
        ReflectionTestUtils.setField(tag1, "id", 1L);
        post.replaceTags(Set.of(PostTag.of(post, tag1)));

        when(postFinder.findByIdOrThrow(10L)).thenReturn(post);

        when(tagRepository.findByNameIn(eq(List.of("tag1", "tag2")))).thenReturn(List.of(tag1));
        Tag tag2 = Tag.of("tag2");
        ReflectionTestUtils.setField(tag2, "id", 2L);
        when(tagRepository.saveAll(any())).thenReturn(List.of(tag2));

        PostUpdateRequest request = new PostUpdateRequest(" new ", List.of("tag1", "tag2"));

        PostUpdateResponse response = postCommandService.update(1L, 10L, request);

        assertThat(post.getContent()).isEqualTo("new");
        assertThat(post.getPostTags()).hasSize(2);
        assertThat(post.getPostTags().stream().map(postTag -> postTag.getTag().getName()))
                .containsExactlyInAnyOrder("tag1", "tag2");
        assertThat(response.content()).isEqualTo("new");
    }

    @Test
    @DisplayName("TC-POST-CMD-03 게시글 삭제: 댓글/좋아요/태그 정리 후 삭제")
    void delete_removesRelatedData() {
        Member member = MemberTestFactory.member(1L);
        Post post = Post.create(member, "content", List.of(PostImage.of(1, "img")));

        when(postFinder.findByIdOrThrow(10L)).thenReturn(post);
        doNothing().when(postValidator).validateOwner(eq(post), eq(1L));

        postCommandService.delete(1L, 10L);

        verify(memberRepository).decrementPostCount(1L);
        verify(commentRepository).deleteByPostId(10L);
        verify(postLikeRepository).deleteByPostId(10L);
        verify(postTagRepository).deleteByPostId(10L);
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("TC-POST-CMD-04 게시글 생성: 태그 누락 시 빈 태그 처리")
    void create_allowsNullTags() {
        Member member = MemberTestFactory.member(1L);
        when(memberFinder.getReferenceById(1L)).thenReturn(member);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            ReflectionTestUtils.setField(post, "id", 11L);
            return post;
        });

        PostCreateRequest request = new PostCreateRequest(
                "content",
                List.of("img1"),
                null
        );

        PostCreateResponse response = postCommandService.create(1L, request);

        Post saved = postCaptor.getValue();
        assertThat(saved.getPostTags()).isEmpty();
        assertThat(response.tags()).isEmpty();
    }

    @Test
    @DisplayName("TC-POST-UPDATE-01 게시글 수정 실패(작성자 아님)")
    void update_failsWhenNotOwner() {
        Member owner = MemberTestFactory.member(1L);
        Post post = Post.create(owner, "content", List.of(PostImage.of(1, "img")));
        when(postFinder.findByIdOrThrow(10L)).thenReturn(post);
        doThrow(new AuthException(AuthErrorCode.ACCESS_DENIED))
                .when(postValidator)
                .validateOwner(eq(post), eq(2L));

        assertThatThrownBy(() -> postCommandService.update(2L, 10L, new PostUpdateRequest("new", List.of())))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("TC-POST-DELETE-01 게시글 삭제 실패(작성자 아님)")
    void delete_failsWhenNotOwner() {
        Member owner = MemberTestFactory.member(1L);
        Post post = Post.create(owner, "content", List.of(PostImage.of(1, "img")));
        when(postFinder.findByIdOrThrow(10L)).thenReturn(post);
        doThrow(new AuthException(AuthErrorCode.ACCESS_DENIED))
                .when(postValidator)
                .validateOwner(eq(post), eq(2L));

        assertThatThrownBy(() -> postCommandService.delete(2L, 10L))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }
}
