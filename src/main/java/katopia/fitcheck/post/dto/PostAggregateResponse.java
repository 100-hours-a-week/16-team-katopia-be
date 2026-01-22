package katopia.fitcheck.post.dto;

import katopia.fitcheck.post.domain.Post;
import lombok.Builder;

@Builder
public record PostAggregateResponse(
        long likeCount,
        long commentCount
) {
    public static PostAggregateResponse of(Post post) {
        return new PostAggregateResponse(post.getLikeCount(), post.getCommentCount());
    }
}
