package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PostContentValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-POST-CONTENT-01 본문 누락(null) 실패")
    void tcPostContent01_nullContent_returnsRequired() {
        Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(new PostContentRequest(null));

        assertSingleViolationWithMessage(violations, PostErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-POST-CONTENT-02 본문 누락(빈 문자열) 실패")
    void tcPostContent02_emptyContent_returnsRequired() {
        Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(new PostContentRequest(""));

        assertSingleViolationWithMessage(violations, PostErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-POST-CONTENT-03 본문 공백만 실패")
    void tcPostContent03_blankContent_returnsRequired() {
        Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(new PostContentRequest("   "));

        assertSingleViolationWithMessage(violations, PostErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-POST-CONTENT-04 본문 길이 초과 실패")
    void tcPostContent04_tooLongContent_returnsTooLong() {
        String content = "a".repeat(201);
        Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(new PostContentRequest(content));

        assertSingleViolationWithMessage(violations, PostErrorCode.CONTENT_TOO_LONG.getCode());
    }

    @Test
    @DisplayName("TC-POST-CONTENT-05 본문 유효성 성공")
    void tcPostContent05_validContent_isValid() {
        Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(new PostContentRequest("valid"));

        assertThat(violations).isEmpty();
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class PostContentRequest {
        @PostContent
        String content;

        PostContentRequest(String content) {
            this.content = content;
        }
    }
}
