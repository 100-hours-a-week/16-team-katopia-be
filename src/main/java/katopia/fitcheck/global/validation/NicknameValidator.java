package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {
    /**
     * 닉네임 허용 문자 정규식
     * - \p{L} : 모든 유니코드 문자 (한글, 영문 등)
     * - \p{N} : 모든 유니코드 숫자
     * - . _   : 점, 언더스코어 허용
     * - ^ $   : 전체 문자열이 해당 패턴과 정확히 일치해야 함
     */
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
        if (ValidationSupport.isOutOfRange(trimmed.length(), MIN_LENGTH, MAX_LENGTH)) {
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
