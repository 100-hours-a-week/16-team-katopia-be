package katopia.fitcheck.search.dto;

import katopia.fitcheck.post.dto.PostSummary;
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
