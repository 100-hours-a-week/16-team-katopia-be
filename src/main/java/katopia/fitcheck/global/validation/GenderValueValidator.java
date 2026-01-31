package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.springframework.util.StringUtils;

import java.util.Locale;

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
                addViolation(context, CommonErrorCode.REQUIRED_VALUE.getCode() + ":" + fieldName);
                return false;
            }
            return true;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!("M".equals(normalized) || "F".equals(normalized))) {
            addViolation(context, MemberErrorCode.INVALID_GENDER_FORMAT.getCode());
            return false;
        }
        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
