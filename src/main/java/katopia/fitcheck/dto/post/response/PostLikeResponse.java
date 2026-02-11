package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.post.Post;
import lombok.Builder;

@Builder
public record PostLikeResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long postId,
        @Schema(description = "좋아요 수", example = "10")
        long likeCount
) {
    public static PostLikeResponse of(Post post) {
        return new PostLikeResponse(post.getId(), post.getLikeCount());
    }

    public static PostLikeResponse of(Long postId, long likeCount) {
        return new PostLikeResponse(postId, likeCount);
    }
}
