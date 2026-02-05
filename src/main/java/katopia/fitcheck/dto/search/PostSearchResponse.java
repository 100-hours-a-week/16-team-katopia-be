package katopia.fitcheck.dto.search;

import katopia.fitcheck.dto.post.response.PostSummary;
import lombok.Builder;

import java.util.List;

@Builder
public record PostSearchResponse(
        List<PostSummary> posts,
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
