package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record PostResponse(
        @Schema(description = "게시글 상세 정보")
        List<PostDetailResponse> posts,
        @Schema(description = Docs.CURSOR_DES, example = Docs.CURSOR)
        String nextCursor
) {
    public static PostResponse of(List<PostDetailResponse> posts, String nextCursor) {
        return PostResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .build();
    }
}
