package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.springframework.util.StringUtils;

public class GenderValueValidator implements ConstraintValidator<GenderValue, String> {

    private boolean required;
    private String fieldName;

    @Override
    public void initialize(GenderValue annotation) {
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

        if (!("M".equals(value) || "F".equals(value))) {
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_GENDER_FORMAT.getCode());
            return false;
        }
        return true;
    }
}
