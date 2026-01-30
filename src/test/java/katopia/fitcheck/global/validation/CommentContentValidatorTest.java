package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommentContentValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-01 본문 누락(null) 실패")
    void tcCommentVal01_nullContent_returnsRequired() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest(null));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-02 본문 누락(빈 문자열) 실패")
    void tcCommentVal02_emptyContent_returnsRequired() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest(""));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-03 본문 공백만 실패")
    void tcCommentVal03_blankContent_returnsRequired() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest("   "));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-04 본문 길이 초과 실패")
    void tcCommentVal04_tooLongContent_returnsTooLong() {
        String content = "a".repeat(201);
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest(content));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_TOO_LONG.getCode());
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-05 본문 유효성 성공")
    void tcCommentVal05_validContent_isValid() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest("valid"));

        assertThat(violations).isEmpty();
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class CommentRequest {
        @CommentContent
        String content;

        CommentRequest(String content) {
            this.content = content;
        }
    }
}
