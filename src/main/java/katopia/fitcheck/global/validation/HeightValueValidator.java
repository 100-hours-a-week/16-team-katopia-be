package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.springframework.util.StringUtils;

public class HeightValueValidator implements ConstraintValidator<HeightValue, String> {
    private static final int MIN_HEIGHT = 50;
    private static final int MAX_HEIGHT = 300;

    private boolean required;
    private String fieldName;

    @Override
    public void initialize(HeightValue annotation) {
        this.required = annotation.required();
        this.fieldName = annotation.fieldName();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            if (required) {
                ValidationSupport.addViolation(context, ValidationSupport.requiredMessage(fieldName));
                return false;
            }
            return true;
        }
        String trimmed = value.trim();
        int parsed;
        try {
            parsed = Integer.parseInt(trimmed);
        } catch (NumberFormatException ex) {
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_HEIGHT_FORMAT.getCode());
            return false;
        }
        if (parsed < MIN_HEIGHT || parsed > MAX_HEIGHT) {
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_HEIGHT_RANGE.getCode());
            return false;
        }
        return true;
    }
}
