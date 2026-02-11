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

class TagListValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-POST-TAG-S-01 태그 null")
    void tcPostTagS01_nullTags_isValid() {
        Set<ConstraintViolation<TagRequest>> violations = validator.validate(new TagRequest(null));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-POST-TAG-S-02 태그 빈 리스트")
    void tcPostTagS02_emptyTags_isValid() {
        Set<ConstraintViolation<TagRequest>> violations = validator.validate(new TagRequest(List.of()));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-POST-TAG-S-03 태그 유효성 성공")
    void tcPostTagS03_validTags_isValid() {
        Set<ConstraintViolation<TagRequest>> violations = validator.validate(new TagRequest(List.of("DAILY", "MINIMAL")));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TC-POST-TAG-F-01 태그 개수 초과")
    void tcPostTagF01_exceedMaxCount_returnsError() {
        List<String> tags = List.of("t1","t2","t3","t4","t5","t6","t7","t8","t9","t10","t11");
        Set<ConstraintViolation<TagRequest>> violations = validator.validate(new TagRequest(tags));

        assertSingleViolationWithMessage(violations, PostErrorCode.TAG_COUNT_EXCEEDED.getCode());
    }

    @Test
    @DisplayName("TC-POST-TAG-F-02 태그 공백 실패")
    void tcPostTagF02_blankTag_returnsError() {
        Set<ConstraintViolation<TagRequest>> violations = validator.validate(new TagRequest(List.of(" ")));

        assertSingleViolationWithMessage(violations, PostErrorCode.TAG_LENGTH_INVALID.getCode());
    }

    @Test
    @DisplayName("TC-POST-TAG-F-03 태그 길이 위반")
    void tcPostTagF03_exceedTagLength_returnsError() {
        String longTag = "a".repeat(21);
        Set<ConstraintViolation<TagRequest>> violations = validator.validate(new TagRequest(List.of(longTag)));

        assertSingleViolationWithMessage(violations, PostErrorCode.TAG_LENGTH_INVALID.getCode());
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class TagRequest {
        @TagList
        List<String> tags;

        TagRequest(List<String> tags) {
            this.tags = tags;
        }
    }
}
