package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponse(
        @Schema(description = Docs.POST_LIST_DES)
        List<PostSummary> posts,
        @Schema(description = Docs.CURSOR_DES, example = Docs.CURSOR)
        String nextCursor
) {
    public static PostListResponse of (List<PostSummary> posts, String nextCursor) {
        return PostListResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .build();
    }
}
