package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {
    public void validateOwner(Comment comment, Long memberId) {
        if (!comment.getMember().getId().equals(memberId)) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }
    }
}
