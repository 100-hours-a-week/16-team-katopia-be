package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import katopia.fitcheck.service.s3.UploadCategory;
import org.springframework.util.StringUtils;

import java.util.List;

public class ImageObjectKeysValidator implements ConstraintValidator<ImageObjectKeys, List<String>> {
    private UploadCategory category;

    @Override
    public void initialize(ImageObjectKeys annotation) {
        this.category = UploadCategory.valueOf(annotation.category());
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty() || value.size() > category.getMaxCount()) {
            ValidationSupport.addViolation(context, resolveErrorCode());
            return false;
        }
        for (String url : value) {
            if (!StringUtils.hasText(url)) {
                ValidationSupport.addViolation(context, resolveErrorCode());
                return false;
            }
        }
        return true;
    }

    private String resolveErrorCode() {
        if (category == UploadCategory.VOTE) {
            return VoteErrorCode.IMAGE_COUNT_INVALID.getCode();
        }
        return PostErrorCode.IMAGE_COUNT_INVALID.getCode();
    }
}
