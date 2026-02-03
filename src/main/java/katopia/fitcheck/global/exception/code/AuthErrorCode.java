package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ResponseCode {
    INVALID_TEMP_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-E-000", "임시 인증 정보가 유효하지 않습니다. 다시 로그인해주세요."),
    INVALID_TEMP_TOKEN_PATH(HttpStatus.BAD_REQUEST, "AUTH-E-001", "임시 인증 정보로는 처리할 수 없는 요청입니다."),
    NOT_FOUND_TEMP_TOKEN(HttpStatus.BAD_REQUEST,"AUTH-E-002", "임시 인증 정보가 존재하지 않습니다."),

    NOT_FOUND_AT(HttpStatus.UNAUTHORIZED, "AUTH-E-010", "인증 정보가 존재하지 않습니다."),
    INVALID_AT(HttpStatus.UNAUTHORIZED, "AUTH-E-011", "인증 정보가 유효하지 않습니다. 다시 로그인해주세요."),
    NOT_FOUND_RT(HttpStatus.UNAUTHORIZED, "AUTH-E-012", "인증 쿠키가 존재하지 않습니다."),
    INVALID_RT(HttpStatus.UNAUTHORIZED, "AUTH-E-013", "인증 정보가 유효하지 않습니다. 다시 로그인해주세요."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-E-014", "올바르지 않은 접근입니다."),

    ALREADY_REGISTERED(HttpStatus.CONFLICT, "AUTH-E-020", "이미 가입된 계정입니다. 로그인을 시도해주세요."),
    WITHDRAWN_MEMBER(HttpStatus.FORBIDDEN, "AUTH-E-021", "탈퇴한 계정입니다. 14일 이후 재가입 가능합니다."),

    UNSUPPORTED_OAUTH2_PRINCIPAL(HttpStatus.UNAUTHORIZED, "AUTH-E-900", "지원하지 않는 인증 정보입니다. 다시 로그인해주세요.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
