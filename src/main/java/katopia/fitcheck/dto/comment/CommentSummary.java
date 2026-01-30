package katopia.fitcheck.dto.comment;

import katopia.fitcheck.domain.comment.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentSummary(
        Long id,
        CommentAuthorResponse author,
        String content,
        LocalDateTime createdAt
) {
    public static CommentSummary of(Comment comment) {
        return CommentSummary.builder()
                .id(comment.getId())
                .author(CommentAuthorResponse.of(comment.getMember()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
