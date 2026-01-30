package katopia.fitcheck.dto.search;

import katopia.fitcheck.dto.post.PostSummary;
import lombok.Builder;

import java.util.List;

@Builder
public record PostSearchResponse(
        List<PostSummary> posts,
        String nextCursor
) {
    public static PostSearchResponse of(List<PostSummary> posts, String nextCursor) {
        return PostSearchResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .build();
    }
}
