package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintValidatorContext;

public final class ValidationSupport {
    private ValidationSupport() {
    }

    public static void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    public static boolean isOutOfRange(int value, int min, int max) {
        return value < min || value > max;
    }

    public static String requiredMessage(String fieldName) {
        return katopia.fitcheck.global.exception.code.CommonErrorCode.REQUIRED_VALUE.getCode() + ":" + fieldName;
    }
}
