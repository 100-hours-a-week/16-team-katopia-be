package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.VoteErrorCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VoteItemIdsValidator implements ConstraintValidator<VoteItemIds, List<Long>> {

    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            ValidationSupport.addViolation(context, VoteErrorCode.VOTE_ITEM_REQUIRED.getCode());
            return false;
        }
        Set<Long> unique = new HashSet<>();
        for (Long id : value) {
            if (id == null || id <= 0) {
                ValidationSupport.addViolation(context, VoteErrorCode.VOTE_ITEM_INVALID.getCode());
                return false;
            }
            if (!unique.add(id)) {
                ValidationSupport.addViolation(context, VoteErrorCode.VOTE_ITEM_DUPLICATED.getCode());
                return false;
            }
        }
        return true;
    }
}
