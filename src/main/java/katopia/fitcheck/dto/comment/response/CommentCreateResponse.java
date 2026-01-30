package katopia.fitcheck.dto.comment.response;

import katopia.fitcheck.domain.comment.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentCreateResponse(
        Long id,
        String content,
        LocalDateTime createdAt
) {
    public static CommentCreateResponse of(Comment comment) {
        return CommentCreateResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
