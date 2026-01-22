package katopia.fitcheck.post.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponse(
        List<PostSummary> posts,
        String nextCursor
) { }
