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

class NicknameValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("TC-NICK-01 필수값 누락(null) 실패")
    void tcNick01_nullNickname_returnsRequiredError() {
        Set<ConstraintViolation<NicknameRequest>> violations = validator.validate(new NicknameRequest(null));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":닉네임");
    }

    @Test
    @DisplayName("TC-NICK-02 필수값 누락(빈 문자열) 실패")
    void tcNick02_emptyNickname_returnsRequiredError() {
        Set<ConstraintViolation<NicknameRequest>> violations = validator.validate(new NicknameRequest(""));

        assertSingleViolationWithMessage(violations, CommonErrorCode.REQUIRED_VALUE.getCode() + ":닉네임");
    }

    @Test
    @DisplayName("TC-NICK-03 공백 포함 실패")
    void tcNick03_whitespaceNickname_returnsWhitespaceError() {
        Set<ConstraintViolation<NicknameRequest>> violations = validator.validate(new NicknameRequest("ab cd"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_NICKNAME_WHITESPACE.getCode());
    }

    @Test
    @DisplayName("TC-NICK-04 길이 하한 위반 실패")
    void tcNick04_tooShortNickname_returnsLengthError() {
        Set<ConstraintViolation<NicknameRequest>> violations = validator.validate(new NicknameRequest("a"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_NICKNAME_LEN.getCode());
    }

    @Test
    @DisplayName("TC-NICK-05 길이 상한 위반 실패")
    void tcNick05_tooLongNickname_returnsLengthError() {
        String nickname = "a".repeat(21);
        Set<ConstraintViolation<NicknameRequest>> violations = validator.validate(new NicknameRequest(nickname));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_NICKNAME_LEN.getCode());
    }

    @Test
    @DisplayName("TC-NICK-06 허용되지 않는 문자 포함 실패")
    void tcNick06_invalidCharacterNickname_returnsCharacterError() {
        Set<ConstraintViolation<NicknameRequest>> violations = validator.validate(new NicknameRequest("ab!"));

        assertSingleViolationWithMessage(violations, MemberErrorCode.INVALID_NICKNAME_CHARACTERS.getCode());
    }

    @Test
    @DisplayName("TC-NICK-07 닉네임 유효성 성공")
    void tcNick07_validNickname_hasNoViolations() {
        Set<ConstraintViolation<NicknameRequest>> violations = validator.validate(new NicknameRequest("user_01"));

        assertThat(violations).isEmpty();
    }

    private void assertSingleViolationWithMessage(Set<? extends ConstraintViolation<?>> violations, String message) {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(message);
    }

    static class NicknameRequest {
        @Nickname(required = true)
        String nickname;

        NicknameRequest(String nickname) {
            this.nickname = nickname;
        }
    }
}
