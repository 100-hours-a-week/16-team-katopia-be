package katopia.fitcheck.comment.dto;

import katopia.fitcheck.comment.domain.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentUpdateResponse(
        Long id,
        String content,
        LocalDateTime updatedAt
) {
    public static CommentUpdateResponse of(Comment comment) {
        return CommentUpdateResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
