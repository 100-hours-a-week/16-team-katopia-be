package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WeightValueValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-WEIGHT-01 필수값 누락(null) 실패")
    void tcWeight01_nullWeight_returnsRequiredError() {
        Set<ConstraintViolation<WeightRequest>> violations = validator.validate(new WeightRequest(null));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":몸무게");
    }

    @Test
    @DisplayName("TC-WEIGHT-02 필수값 누락(빈 문자열) 실패")
    void tcWeight02_emptyWeight_returnsRequiredError() {
        Set<ConstraintViolation<WeightRequest>> violations = validator.validate(new WeightRequest(""));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":몸무게");
    }

    @Test
    @DisplayName("TC-WEIGHT-03 숫자 형식 오류")
    void tcWeight03_invalidFormat_returnsFormatError() {
        Set<ConstraintViolation<WeightRequest>> violations = validator.validate(new WeightRequest("7a"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_WEIGHT_FORMAT.getCode());
    }

    @Test
    @DisplayName("TC-WEIGHT-04 범위 하한 위반")
    void tcWeight04_belowMin_returnsRangeError() {
        Set<ConstraintViolation<WeightRequest>> violations = validator.validate(new WeightRequest("19"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_WEIGHT_RANGE.getCode());
    }

    @Test
    @DisplayName("TC-WEIGHT-05 범위 상한 위반")
    void tcWeight05_aboveMax_returnsRangeError() {
        Set<ConstraintViolation<WeightRequest>> violations = validator.validate(new WeightRequest("501"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_WEIGHT_RANGE.getCode());
    }

    @Test
    @DisplayName("TC-WEIGHT-06 유효성 성공")
    void tcWeight06_validWeight_isValid() {
        Set<ConstraintViolation<WeightRequest>> violations = validator.validate(new WeightRequest("70"));

        assertThat(violations).isEmpty();
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class WeightRequest {
        @WeightValue(required = true)
        String weight;

        WeightRequest(String weight) {
            this.weight = weight;
        }
    }
}
