package katopia.fitcheck.dto.post.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponse(
        List<PostSummary> posts,
        String nextCursor
) {
    public static PostListResponse of (List<PostSummary> posts, String nextCursor) {
        return PostListResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .build();
    }
}
