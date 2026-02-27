package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.policy.Policy;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(Policy.NICKNAME_REGEX);
    private boolean required;
    private String fieldName;

    @Override
    public void initialize(Nickname annotation) {
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
        if (value.chars().anyMatch(Character::isWhitespace)) {
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_NICKNAME_WHITESPACE.getCode());
            return false;
        }
        String trimmed = value.trim();
        if (ValidationSupport.isOutOfRange(
                trimmed.length(), Policy.NICKNAME_MIN_LENGTH, Policy.NICKNAME_MAX_LENGTH)) {
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_NICKNAME_LEN.getCode());
            return false;
        }
        if (!NICKNAME_PATTERN.matcher(trimmed).matches()) {
            ValidationSupport.addViolation(context, MemberErrorCode.INVALID_NICKNAME_CHARACTERS.getCode());
            return false;
        }
        return true;
    }
}
