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

class HeightValueValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-HEIGHT-01 필수값 누락(null) 실패")
    void tcHeight01_nullHeight_returnsRequiredError() {
        Set<ConstraintViolation<HeightRequest>> violations = validator.validate(new HeightRequest(null));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":키");
    }

    @Test
    @DisplayName("TC-HEIGHT-02 필수값 누락(빈 문자열) 실패")
    void tcHeight02_emptyHeight_returnsRequiredError() {
        Set<ConstraintViolation<HeightRequest>> violations = validator.validate(new HeightRequest(""));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":키");
    }

    @Test
    @DisplayName("TC-HEIGHT-03 숫자 형식 오류")
    void tcHeight03_invalidFormat_returnsFormatError() {
        Set<ConstraintViolation<HeightRequest>> violations = validator.validate(new HeightRequest("17a"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_HEIGHT_FORMAT.getCode());
    }

    @Test
    @DisplayName("TC-HEIGHT-04 범위 하한 위반")
    void tcHeight04_belowMin_returnsRangeError() {
        Set<ConstraintViolation<HeightRequest>> violations = validator.validate(new HeightRequest("49"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_HEIGHT_RANGE.getCode());
    }

    @Test
    @DisplayName("TC-HEIGHT-05 범위 상한 위반")
    void tcHeight05_aboveMax_returnsRangeError() {
        Set<ConstraintViolation<HeightRequest>> violations = validator.validate(new HeightRequest("301"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_HEIGHT_RANGE.getCode());
    }

    @Test
    @DisplayName("TC-HEIGHT-06 유효성 성공")
    void tcHeight06_validHeight_isValid() {
        Set<ConstraintViolation<HeightRequest>> violations = validator.validate(new HeightRequest("170"));

        assertThat(violations).isEmpty();
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class HeightRequest {
        @HeightValue(required = true)
        String height;

        HeightRequest(String height) {
            this.height = height;
        }
    }
}
