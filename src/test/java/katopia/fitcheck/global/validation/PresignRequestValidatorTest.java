package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import katopia.fitcheck.dto.s3.PresignRequest;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.service.s3.UploadCategory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PresignRequestValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-01 요청 null")
    void tcPresignVal01_nullRequest_returnsError() {
        Set<ConstraintViolation<Wrapper>> violations = validator.validate(new Wrapper(null));

        assertSingleViolationWithMessage(violations, CommonErrorCode.INVALID_INPUT_VALUE.getCode());
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-02 category 누락")
    void tcPresignVal02_missingCategory_returnsError() {
        PresignRequest request = new PresignRequest(null, List.of("png"));

        assertInvalid(request);
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-03 extensions 누락")
    void tcPresignVal03_missingExtensions_returnsError() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, null);

        assertInvalid(request);
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-04 extensions 빈 리스트")
    void tcPresignVal04_emptyExtensions_returnsError() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of());

        assertInvalid(request);
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-05 개수 초과")
    void tcPresignVal05_exceedMaxCount_returnsError() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("png", "jpg"));

        assertInvalid(request);
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-06 허용되지 않는 확장자")
    void tcPresignVal06_invalidExtension_returnsError() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("exe"));

        assertInvalid(request);
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-07 확장자 정규화 허용")
    void tcPresignVal07_normalizedExtension_isValid() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of(".PNG"));

        assertValid(request);
    }

    @Test
    @DisplayName("TC-PRESIGN-VAL-08 최대 개수 허용")
    void tcPresignVal08_maxCount_isValid() {
        PresignRequest request = new PresignRequest(UploadCategory.POST, List.of("png", "jpg", "webp"));

        assertValid(request);
    }

    private void assertInvalid(PresignRequest request) {
        Set<ConstraintViolation<Wrapper>> violations = validator.validate(new Wrapper(request));
        assertSingleViolationWithMessage(violations, CommonErrorCode.INVALID_INPUT_VALUE.getCode());
    }

    private void assertValid(PresignRequest request) {
        Set<ConstraintViolation<Wrapper>> violations = validator.validate(new Wrapper(request));
        assertThat(violations).isEmpty();
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    @ValidPresignRequest
    static class Wrapper {
        @Valid
        PresignRequest request;

        Wrapper(PresignRequest request) {
            this.request = request;
        }
    }
}
