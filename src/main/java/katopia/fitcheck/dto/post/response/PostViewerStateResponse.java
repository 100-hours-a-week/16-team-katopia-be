package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

@Builder
public record PostViewerStateResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = "게시글 좋아요 여부", example = "false")
        boolean isLiked,
        @Schema(description = "게시글 북마크 여부", example = "false")
        boolean isBookmarked
) {
    public static PostViewerStateResponse of(Long postId, boolean isLiked, boolean isBookmarked) {
        return new PostViewerStateResponse(postId, isLiked, isBookmarked);
    }
}
