package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import katopia.fitcheck.global.policy.Policy;
import org.springframework.util.StringUtils;

public class VoteTitleValidator implements ConstraintValidator<VoteTitle, String> {
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
        if (ValidationSupport.isOutOfRange(
                trimmed.length(),
                Policy.VOTE_TITLE_MIN_LENGTH,
                Policy.VOTE_TITLE_MAX_LENGTH)
        ) {
            ValidationSupport.addViolation(context, VoteErrorCode.TITLE_TOO_LONG.getCode());
            return false;
        }
        return true;
    }
}
