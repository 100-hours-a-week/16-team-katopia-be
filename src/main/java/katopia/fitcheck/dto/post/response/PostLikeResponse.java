package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

@Builder
public record PostLikeResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long postId,
        @Schema(description = Docs.POST_LIKE_COUNT_DES, example = "0")
        long likeCount
) {
    public static PostLikeResponse of(Post post) {
        return new PostLikeResponse(post.getId(), post.getLikeCount());
    }

    public static PostLikeResponse of(Long postId, long likeCount) {
        return new PostLikeResponse(postId, likeCount);
    }
}
