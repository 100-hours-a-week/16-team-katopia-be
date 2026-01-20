package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ResponseCode {
    INVALID_TEMP_AUTH(HttpStatus.UNAUTHORIZED, "AUTH-E-001", "임시 인증 정보가 유효하지 않습니다. 다시 로그인해주세요."),

    NOT_FOUND_AT(HttpStatus.UNAUTHORIZED, "AUTH-E-002", "인증 정보가 존재하지 않습니다."),
    INVALID_AT(HttpStatus.UNAUTHORIZED, "AUTH-E-003", "인증 정보가 유효하지 않습니다. 다시 로그인해주세요."),
    NOT_FOUND_RT(HttpStatus.UNAUTHORIZED, "AUTH-E-004", "인증 쿠키가 존재하지 않습니다."),
    INVALID_RT(HttpStatus.UNAUTHORIZED, "AUTH-E-005", "인증 정보가 유효하지 않습니다. 다시 로그인해주세요."),

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-E-006", "올바르지 않은 접근입니다."),
    ALREADY_REGISTERED(HttpStatus.CONFLICT, "AUTH-E-007", "이미 가입된 계정입니다. 로그인을 시도해주세요.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
