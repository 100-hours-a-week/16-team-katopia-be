package katopia.fitcheck.post.dto;

import katopia.fitcheck.post.domain.Post;
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
