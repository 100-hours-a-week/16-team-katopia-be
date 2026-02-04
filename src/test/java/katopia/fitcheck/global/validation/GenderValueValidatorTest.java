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

class GenderValueValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-GENDER-01 필수값 누락(null) 실패")
    void tcGender01_nullGender_returnsRequiredError() {
        Set<ConstraintViolation<GenderRequest>> violations = validator.validate(new GenderRequest(null));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":성별");
    }

    @Test
    @DisplayName("TC-GENDER-02 필수값 누락(빈 문자열) 실패")
    void tcGender02_emptyGender_returnsRequiredError() {
        Set<ConstraintViolation<GenderRequest>> violations = validator.validate(new GenderRequest(""));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":성별");
    }

    @Test
    @DisplayName("TC-GENDER-03 유효하지 않은 값 실패")
    void tcGender03_invalidGender_returnsFormatError() {
        Set<ConstraintViolation<GenderRequest>> violations = validator.validate(new GenderRequest("X"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_GENDER_FORMAT.getCode());
    }

    @Test
    @DisplayName("TC-GENDER-04 유효성 성공(대문자)")
    void tcGender04_uppercaseGender_isValid() {
        Set<ConstraintViolation<GenderRequest>> violations = validator.validate(new GenderRequest("M"));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-GENDER-05 유효하지 않은 값 실패(소문자)")
    void tcGender05_lowercaseGender_isValid() {
        Set<ConstraintViolation<GenderRequest>> violations = validator.validate(new GenderRequest("f"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_GENDER_FORMAT.getCode());
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class GenderRequest {
        @GenderValue(required = true)
        String gender;

        GenderRequest(String gender) {
            this.gender = gender;
        }
    }
}
