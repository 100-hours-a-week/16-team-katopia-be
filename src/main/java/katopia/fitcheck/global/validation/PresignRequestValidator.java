package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import katopia.fitcheck.dto.s3.PresignRequest;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.service.s3.UploadCategory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

public class PresignRequestValidator implements ConstraintValidator<ValidPresignRequest, PresignRequest> {
    @Override
    public boolean isValid(PresignRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            ValidationSupport.addViolation(context, CommonErrorCode.INVALID_INPUT_VALUE.getCode());
            return false;
        }
        UploadCategory category = value.category();
        List<String> extensions = value.extensions();
        if (category == null || extensions == null || extensions.isEmpty()) {
            ValidationSupport.addViolation(context, CommonErrorCode.INVALID_INPUT_VALUE.getCode());
            return false;
        }
        if (!isCountAllowed(category, extensions.size())) {
            ValidationSupport.addViolation(context, CommonErrorCode.INVALID_INPUT_VALUE.getCode());
            return false;
        }
        for (String ext : extensions) {
            String normalized = normalizeExtension(ext);
            if (!Policy.PRESIGN_ALLOWED_EXTENSIONS.contains(normalized)) {
                ValidationSupport.addViolation(context, CommonErrorCode.INVALID_INPUT_VALUE.getCode());
                return false;
            }
        }
        return true;
    }

    private String normalizeExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return "";
        }
        String trimmed = extension.trim().toLowerCase(Locale.ROOT);
        if (trimmed.startsWith(".")) {
            trimmed = trimmed.substring(1);
        }
        return trimmed;
    }

    private boolean isCountAllowed(UploadCategory category, int size) {
        return size >= 1 && size <= category.getMaxCount();
    }
}
