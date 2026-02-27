package katopia.fitcheck.global.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import katopia.fitcheck.dto.comment.request.CommentRequest;
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
    @DisplayName("TC-COMMENT-VAL-S-01 본문 유효성 성공")
    void tcCommentValS01_validContent_isValid() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest("valid"));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-F-01 본문 누락(null) 실패")
    void tcCommentValF01_nullContent_returnsRequired() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest(null));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-F-02 본문 누락(빈 문자열) 실패")
    void tcCommentValF02_emptyContent_returnsRequired() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest(""));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-F-03 본문 공백만 실패")
    void tcCommentValF03_blankContent_returnsRequired() {
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest("   "));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_REQUIRED.getCode());
    }

    @Test
    @DisplayName("TC-COMMENT-VAL-F-04 본문 길이 초과 실패")
    void tcCommentValF04_tooLongContent_returnsTooLong() {
        String content = "a".repeat(201);
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(new CommentRequest(content));

        assertSingleViolationWithMessage(violations, CommentErrorCode.CONTENT_TOO_LONG.getCode());
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }
}
