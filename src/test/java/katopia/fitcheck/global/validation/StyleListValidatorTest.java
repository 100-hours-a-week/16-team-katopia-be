package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StyleListValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-STYLE-S-01 스타일 없음(null) 성공")
    void tcStyleS01_nullList_isValid() {
        Set<ConstraintViolation<StyleRequest>> violations = validator.validate(new StyleRequest(null));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-STYLE-S-02 스타일 빈 리스트 성공")
    void tcStyleS02_emptyList_isValid() {
        Set<ConstraintViolation<StyleRequest>> violations = validator.validate(new StyleRequest(List.of()));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-STYLE-S-03 유효성 성공")
    void tcStyleS03_validStyles_isValid() {
        Set<ConstraintViolation<StyleRequest>> violations = validator.validate(new StyleRequest(List.of("MINIMAL", "CASUAL")));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-STYLE-F-01 스타일 개수 초과 실패")
    void tcStyleF01_exceedLimit_returnsError() {
        Set<ConstraintViolation<StyleRequest>> violations = validator.validate(
                new StyleRequest(List.of("MINIMAL", "CASUAL", "SPORTY"))
        );

        assertSingleViolationWithMessage(violations, MemberErrorCode.STYLE_LIMIT_EXCEEDED.getCode());
    }

    @Test
    @DisplayName("TC-STYLE-F-02 공백 포함 스타일 실패")
    void tcStyleF02_blankStyle_returnsError() {
        Set<ConstraintViolation<StyleRequest>> violations = validator.validate(new StyleRequest(List.of(" ")));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_STYLE_FORMAT.getCode());
    }

    @Test
    @DisplayName("TC-STYLE-F-03 유효하지 않은 값 실패")
    void tcStyleF03_invalidStyle_returnsError() {
        Set<ConstraintViolation<StyleRequest>> violations = validator.validate(new StyleRequest(List.of("UNKNOWN")));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_STYLE_FORMAT.getCode());
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class StyleRequest {
        @StyleList
        List<String> style;

        StyleRequest(List<String> style) {
            this.style = style;
        }
    }
}
