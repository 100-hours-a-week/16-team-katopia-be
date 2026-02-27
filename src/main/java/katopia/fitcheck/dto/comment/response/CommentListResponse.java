package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record CommentListResponse(
        @Schema(description = Docs.COMMENT_CONTENT_LIST_DES)
        List<CommentSummary> comments,
        @Schema(description = Docs.CURSOR_DES, example = Docs.CURSOR)
        String nextCursor
) {
    public static CommentListResponse of(List<CommentSummary> comments, String nextCursor) {
        return CommentListResponse.builder()
                .comments(comments)
                .nextCursor(nextCursor)
                .build();
    }
}
