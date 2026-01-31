package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.springframework.util.StringUtils;

public class WeightValueValidator implements ConstraintValidator<WeightValue, String> {
    private static final int MIN_WEIGHT = 20;
    private static final int MAX_WEIGHT = 500;

    private boolean required;
    private String fieldName;

    @Override
    public void initialize(WeightValue annotation) {
        this.required = annotation.required();
        this.fieldName = annotation.fieldName();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            if (required) {
                addViolation(context, CommonErrorCode.REQUIRED_VALUE.getCode() + ":" + fieldName);
                return false;
            }
            return true;
        }
        String trimmed = value.trim();
        int parsed;
        try {
            parsed = Integer.parseInt(trimmed);
        } catch (NumberFormatException ex) {
            addViolation(context, MemberErrorCode.INVALID_WEIGHT_FORMAT.getCode());
            return false;
        }
        if (parsed < MIN_WEIGHT || parsed > MAX_WEIGHT) {
            addViolation(context, MemberErrorCode.INVALID_WEIGHT_RANGE.getCode());
            return false;
        }
        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
