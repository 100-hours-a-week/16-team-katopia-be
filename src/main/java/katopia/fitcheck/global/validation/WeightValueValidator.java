package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.policy.Policy;
import org.springframework.util.StringUtils;

public class WeightValueValidator implements ConstraintValidator<WeightValue, String> {
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
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_WEIGHT_FORMAT.getCode());
            return false;
        }
        if (parsed < Policy.WEIGHT_MIN || parsed > Policy.WEIGHT_MAX) {
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_WEIGHT_RANGE.getCode());
            return false;
        }
        return true;
    }
}
