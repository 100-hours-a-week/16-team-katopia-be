package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.post.Post;
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
