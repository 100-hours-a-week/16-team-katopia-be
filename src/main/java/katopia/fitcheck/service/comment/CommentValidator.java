package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CommentValidator {

    private static final int MAX_CONTENT_LENGTH = 200;

    public String validateContent(String content) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(CommentErrorCode.CONTENT_REQUIRED);
        }
        String trimmed = content.trim();
        if (trimmed.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(CommentErrorCode.CONTENT_TOO_LONG);
        }
        return trimmed;
    }

    public void validateOwner(Comment comment, Long memberId) {
        if (!comment.getMember().getId().equals(memberId)) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }
    }
}
