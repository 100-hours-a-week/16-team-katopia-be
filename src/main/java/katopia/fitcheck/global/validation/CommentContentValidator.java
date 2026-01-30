package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import org.springframework.util.StringUtils;

public class CommentContentValidator implements ConstraintValidator<CommentContent, String> {
    private static final int MAX_LENGTH = 200;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            addViolation(context, CommentErrorCode.CONTENT_REQUIRED.getCode());
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.length() < 1) {
            addViolation(context, CommentErrorCode.CONTENT_REQUIRED.getCode());
            return false;
        }
        if (trimmed.length() > MAX_LENGTH) {
            addViolation(context, CommentErrorCode.CONTENT_TOO_LONG.getCode());
            return false;
        }
        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
