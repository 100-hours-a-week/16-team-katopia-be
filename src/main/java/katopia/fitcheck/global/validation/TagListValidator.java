package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.global.policy.Policy;
import org.springframework.util.StringUtils;

import java.util.List;

public class TagListValidator implements ConstraintValidator<TagList, List<String>> {
    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        if (value.size() > Policy.TAG_MAX_COUNT) {
            ValidationSupport.addViolation(context, PostErrorCode.TAG_COUNT_EXCEEDED.getCode());
            return false;
        }
        for (String tag : value) {
            if (!StringUtils.hasText(tag)) {
                ValidationSupport.addViolation(context, PostErrorCode.TAG_LENGTH_INVALID.getCode());
                return false;
            }
            String normalized = normalize(tag);
            if (ValidationSupport.isOutOfRange(
                    normalized.length(), Policy.TAG_MIN_LENGTH, Policy.TAG_MAX_LENGTH)) {
                ValidationSupport.addViolation(context, PostErrorCode.TAG_LENGTH_INVALID.getCode());
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

}
