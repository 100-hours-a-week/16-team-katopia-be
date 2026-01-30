package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import org.springframework.util.StringUtils;

import java.util.List;

public class TagListValidator implements ConstraintValidator<TagList, List<String>> {
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 20;
    private static final int MAX_COUNT = 10;

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        if (value.size() > MAX_COUNT) {
            addViolation(context, PostErrorCode.TAG_COUNT_EXCEEDED.getCode());
            return false;
        }
        for (String tag : value) {
            if (!StringUtils.hasText(tag)) {
                addViolation(context, PostErrorCode.TAG_LENGTH_INVALID.getCode());
                return false;
            }
            String normalized = normalize(tag);
            int length = normalized.length();
            if (length < MIN_LENGTH || length > MAX_LENGTH) {
                addViolation(context, PostErrorCode.TAG_LENGTH_INVALID.getCode());
                return false;
            }
        }
        return true;
    }

    private String normalize(String tag) {
        String trimmed = tag.trim();
        if (trimmed.startsWith("#")) {
            return trimmed.substring(1).trim();
        }
        return trimmed;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
