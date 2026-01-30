package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {
    /** 유니코드 문자/숫자와 . _ 만 허용하는 전체 일치 정규식 */
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\p{L}\\p{N}._]+$");
    private static final int MIN_LENGTH = 2, MAX_LENGTH = 20;

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
                addViolation(context, requiredMessage());
                return false;
            }
            return true;
        }
        if (value.chars().anyMatch(Character::isWhitespace)) {
            addViolation(context, MemberErrorCode.INVALID_NICKNAME_WHITESPACE.getCode());
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            addViolation(context, MemberErrorCode.INVALID_NICKNAME_LEN.getCode());
            return false;
        }
        if (!NICKNAME_PATTERN.matcher(trimmed).matches()) {
            addViolation(context, MemberErrorCode.INVALID_NICKNAME_CHARACTERS.getCode());
            return false;
        }
        return true;
    }

    private String requiredMessage() {
        return CommonErrorCode.REQUIRED_VALUE.getCode() + ":" + fieldName;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
