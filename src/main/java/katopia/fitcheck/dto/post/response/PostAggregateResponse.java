package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.post.Post;
import lombok.Builder;

@Builder
public record PostAggregateResponse(
        @Schema(description = "좋아요 수", example = "10")
        long likeCount,
        @Schema(description = "댓글 수", example = "2")
        long commentCount
) {
    public static PostAggregateResponse of(Post post) {
        return new PostAggregateResponse(post.getLikeCount(), post.getCommentCount());
    }
}
