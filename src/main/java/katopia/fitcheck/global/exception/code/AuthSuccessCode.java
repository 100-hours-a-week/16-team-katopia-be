package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessCode implements ResponseCode {
    NEW_MEMBER_NEED_INFO(HttpStatus.OK, "AUTH-S-000", "신규 회원입니다. 추가 정보 입력이 필요합니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "AUTH-S-001", "회원 가입이 완료되었습니다."),
    MEMBER_WITHDRAWN(HttpStatus.NO_CONTENT, "AUTH-S-002", "회원 탈퇴가 완료되었습니다."),

    LOGIN_SUCCESS(HttpStatus.CREATED, "AUTH-S-010", "로그인이 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH-S-011", "로그아웃이 완료되었습니다."),

    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "AUTH-S-020", "엑세스 토큰이 갱신되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
