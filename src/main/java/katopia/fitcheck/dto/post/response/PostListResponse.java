package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponse(
        @Schema(description = "게시글 목록")
        List<PostSummary> posts,
        @Schema(description = "커서(createdAt|id)", example = SwaggerExamples.TIMESTAMP_EXAMPLE + "|5")
        String nextCursor
) {
    public static PostListResponse of (List<PostSummary> posts, String nextCursor) {
        return PostListResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .build();
    }
}
