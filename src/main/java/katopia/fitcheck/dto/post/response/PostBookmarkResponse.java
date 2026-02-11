package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import lombok.Builder;

@Builder
public record PostBookmarkResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long postId,
        @Schema(description = "게시글 북마크 여부", example = "true")
        boolean isBookmarked
) {
    public static PostBookmarkResponse of(Long postId, boolean isBookmarked) {
        return new PostBookmarkResponse(postId, isBookmarked);
    }
}
