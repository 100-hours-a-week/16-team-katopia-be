package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.post.Post;
import lombok.Builder;

@Builder
public record PostLikeResponse(
        Long postId,
        long likeCount
) {
    public static PostLikeResponse of(Post post) {
        return new PostLikeResponse(post.getId(), post.getLikeCount());
    }
}
