package katopia.fitcheck.dto.comment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.util.List;

@Builder
public record CommentListResponse(
        @Schema(description = "댓글 목록")
        List<CommentSummary> comments,
        @Schema(description = "커서(createdAt|id)", example = SwaggerExamples.TIMESTAMP_EXAMPLE + "|5")
        String nextCursor
) {
    public static CommentListResponse of(List<CommentSummary> comments, String nextCursor) {
        return CommentListResponse.builder()
                .comments(comments)
                .nextCursor(nextCursor)
                .build();
    }
}
