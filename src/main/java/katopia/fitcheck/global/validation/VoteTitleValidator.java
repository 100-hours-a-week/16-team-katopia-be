package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import org.springframework.util.StringUtils;

public class VoteTitleValidator implements ConstraintValidator<VoteTitle, String> {
    private static final int MAX_LENGTH = 20;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            ValidationSupport.addViolation(context, VoteErrorCode.TITLE_REQUIRED.getCode());
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            ValidationSupport.addViolation(context, VoteErrorCode.TITLE_REQUIRED.getCode());
            return false;
        }
        if (ValidationSupport.isOutOfRange(trimmed.length(), 1, MAX_LENGTH)) {
            ValidationSupport.addViolation(context, VoteErrorCode.TITLE_TOO_LONG.getCode());
            return false;
        }
        return true;
    }
}
