package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessCode implements ResponseCode {
    NEW_MEMBER_NEED_INFO(HttpStatus.OK, "AUTH-S-001", "신규 회원입니다. 추가 정보 입력이 필요합니다."),
    LOGIN_SUCCESS(HttpStatus.CREATED, "AUTH-S-002", "로그인이 완료되었습니다."),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "AUTH-S-003", "토큰이 성공적으로 갱신되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH-S-004", "로그아웃 처리가 완료되었습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "AUTH-S-005", "회원가입이 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
