package katopia.fitcheck.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.dto.post.response.PostSummary;
import lombok.Builder;

import java.util.List;

@Builder
public record PostSearchResponse(
        @Schema(description = "게시글 검색 결과")
        List<PostSummary> posts,
        @Schema(description = "커서(createdAt|id)", example = SwaggerExamples.TIMESTAMP_EXAMPLE + "|5")
        String nextCursor
) implements SearchResultCount {
    public static PostSearchResponse of(List<PostSummary> posts, String nextCursor) {
        return PostSearchResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .build();
    }

    @Override
    public int resultCount() {
        return posts == null ? 0 : posts.size();
    }
}
