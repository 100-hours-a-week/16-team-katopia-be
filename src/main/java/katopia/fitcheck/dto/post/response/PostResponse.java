package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record PostResponse(
        @Schema(description = "게시글 상세 목록")
        List<PostDetailResponse> posts,
        @Schema(description = "다음 커서")
        String nextCursor
) {
    public static PostResponse of(List<PostDetailResponse> posts, String nextCursor) {
        return PostResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .build();
    }
}
