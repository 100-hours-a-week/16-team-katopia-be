package katopia.fitcheck.dto.comment;

import lombok.Builder;

import java.util.List;

@Builder
public record CommentListResponse(
        List<CommentSummary> comments,
        String nextCursor
) {
    public static CommentListResponse of(List<CommentSummary> comments, String nextCursor) {
        return CommentListResponse.builder()
                .comments(comments)
                .nextCursor(nextCursor)
                .build();
    }
}
