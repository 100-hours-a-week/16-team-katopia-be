package katopia.fitcheck.dto.post;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.dto.post.response.PostLikeResponse;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostLikeResponseTest {

    @Test
    @DisplayName("TC-POST-LIKE-RESP-01 좋아요 응답 변환")
    void tcPostLikeResp01_of_returnsIdAndLikeCount() {
        Member author = MemberTestFactory.member(1L);
        Post post = Post.create(author, "content", List.of(PostImage.of(1, "img")));
        ReflectionTestUtils.setField(post, "id", 10L);
        ReflectionTestUtils.setField(post, "likeCount", 7L);

        PostLikeResponse response = PostLikeResponse.of(post);

        assertThat(response.postId()).isEqualTo(10L);
        assertThat(response.likeCount()).isEqualTo(7L);
    }
}
