package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.service.s3.UploadCategory;
import org.springframework.util.StringUtils;

import java.util.List;

public class ImageUrlsValidator implements ConstraintValidator<ImageUrls, List<String>> {
    private UploadCategory category;

    @Override
    public void initialize(ImageUrls annotation) {
        this.category = UploadCategory.valueOf(annotation.category());
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty() || value.size() > category.getMaxCount()) {
            addViolation(context, PostErrorCode.IMAGE_COUNT_INVALID.getCode());
            return false;
        }
        for (String url : value) {
            if (!StringUtils.hasText(url)) {
                addViolation(context, PostErrorCode.IMAGE_COUNT_INVALID.getCode());
                return false;
            }
        }
        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
