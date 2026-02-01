package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ImageObjectKeysValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-IMAGE-01 이미지 리스트 null")
    void tcImage01_nullList_returnsError() {
        Set<ConstraintViolation<ImageRequest>> violations = validator.validate(new ImageRequest(null));

        assertSingleViolationWithMessage(violations, PostErrorCode.IMAGE_COUNT_INVALID.getCode());
    }

    @Test
    @DisplayName("TC-IMAGE-02 이미지 리스트 빈 값")
    void tcImage02_emptyList_returnsError() {
        Set<ConstraintViolation<ImageRequest>> violations = validator.validate(new ImageRequest(List.of()));

        assertSingleViolationWithMessage(violations, PostErrorCode.IMAGE_COUNT_INVALID.getCode());
    }

    @Test
    @DisplayName("TC-IMAGE-03 이미지 개수 초과")
    void tcImage03_exceedMaxCount_returnsError() {
        Set<ConstraintViolation<ImageRequest>> violations = validator.validate(
                new ImageRequest(List.of("1", "2", "3", "4"))
        );

        assertSingleViolationWithMessage(violations, PostErrorCode.IMAGE_COUNT_INVALID.getCode());
    }

    @Test
    @DisplayName("TC-IMAGE-04 이미지 오브젝트 키 공백")
    void tcImage04_blankUrl_returnsError() {
        Set<ConstraintViolation<ImageRequest>> violations = validator.validate(new ImageRequest(List.of(" ")));

        assertSingleViolationWithMessage(violations, PostErrorCode.IMAGE_COUNT_INVALID.getCode());
    }

    @Test
    @DisplayName("TC-IMAGE-05 이미지 유효성 성공")
    void tcImage05_validUrls_isValid() {
        Set<ConstraintViolation<ImageRequest>> violations = validator.validate(new ImageRequest(List.of("url1", "url2")));

        assertThat(violations).isEmpty();
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class ImageRequest {
        @ImageObjectKeys(category = "POST")
        List<String> imageObjectKeys;

        ImageRequest(List<String> imageObjectKeys) {
            this.imageObjectKeys = imageObjectKeys;
        }
    }
}
