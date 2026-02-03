package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import org.springframework.util.StringUtils;

public class CommentContentValidator implements ConstraintValidator<CommentContent, String> {
    private static final int MIN_LENGTH = 1, MAX_LENGTH = 200;

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
        if (ValidationSupport.isOutOfRange(trimmed.length(), MIN_LENGTH, MAX_LENGTH)) {
            ValidationSupport.addViolation(context, CommentErrorCode.CONTENT_TOO_LONG.getCode());
            return false;
        }
        return true;
    }
}
