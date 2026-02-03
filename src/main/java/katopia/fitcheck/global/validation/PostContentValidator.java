package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import org.springframework.util.StringUtils;

public class PostContentValidator implements ConstraintValidator<PostContent, String> {
    private static final int MAX_LENGTH = 200;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            ValidationSupport.addViolation(context, PostErrorCode.CONTENT_REQUIRED.getCode());
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            ValidationSupport.addViolation(context, PostErrorCode.CONTENT_REQUIRED.getCode());
            return false;
        }
        if (ValidationSupport.isOutOfRange(trimmed.length(), 1, MAX_LENGTH)) {
            ValidationSupport.addViolation(context, PostErrorCode.CONTENT_TOO_LONG.getCode());
            return false;
        }
        return true;
    }
}
