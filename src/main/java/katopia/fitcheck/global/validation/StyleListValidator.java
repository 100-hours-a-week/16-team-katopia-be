package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.domain.member.StyleType;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

public class StyleListValidator implements ConstraintValidator<StyleList, List<String>> {
    private static final int MAX_STYLE_COUNT = 2;

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        if (value.size() > MAX_STYLE_COUNT) {
            ValidationSupport.addViolation(context, MemberErrorCode.STYLE_LIMIT_EXCEEDED.getCode());
            return false;
        }
        for (String style : value) {
            if (!StringUtils.hasText(style)) {
                ValidationSupport.addViolation(context, MemberErrorCode.INVALID_STYLE_FORMAT.getCode());
                return false;
            }
            try {
                StyleType.valueOf(style.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                ValidationSupport.addViolation(context, MemberErrorCode.INVALID_STYLE_FORMAT.getCode());
                return false;
            }
        }
        return true;
    }
}
