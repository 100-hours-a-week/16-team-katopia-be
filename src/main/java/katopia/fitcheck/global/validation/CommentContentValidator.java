package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import katopia.fitcheck.global.policy.Policy;
import org.springframework.util.StringUtils;

public class CommentContentValidator implements ConstraintValidator<CommentContent, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            ValidationSupport.addViolation(context, CommentErrorCode.CONTENT_REQUIRED.getCode());
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            ValidationSupport.addViolation(context, CommentErrorCode.CONTENT_REQUIRED.getCode());
            return false;
        }
        if (ValidationSupport.isOutOfRange(
                trimmed.length(),
                Policy.COMMENT_CONTENT_MIN_LENGTH,
                Policy.COMMENT_CONTENT_MAX_LENGTH)) {
            ValidationSupport.addViolation(context, CommentErrorCode.CONTENT_TOO_LONG.getCode());
            return false;
        }
        return true;
    }
}
