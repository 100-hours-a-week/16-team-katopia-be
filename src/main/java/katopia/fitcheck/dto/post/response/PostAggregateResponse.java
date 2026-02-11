package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

@Builder
public record PostAggregateResponse(
        @Schema(description = Docs.POST_LIKE_COUNT_DES, example = "0")
        long likeCount,
        @Schema(description = Docs.COMMENT_COUNT_DES, example = "0")
        long commentCount
) {
    public static PostAggregateResponse of(Post post) {
        return new PostAggregateResponse(post.getLikeCount(), post.getCommentCount());
    }
}
