package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ResponseCode {
    INVALID_NICKNAME_LEN(HttpStatus.BAD_REQUEST, "MEMBER-E-001", "닉네임은 최초 2자, 최대 20자까지 입력 가능합니다."),
    INVALID_NICKNAME_CHARACTERS(HttpStatus.BAD_REQUEST, "MEMBER-E-002", ".(온점), _(아래 밑줄) 외의 특수문자는 포함할 수 없습니다."),
    INVALID_NICKNAME_WHITESPACE(HttpStatus.BAD_REQUEST, "MEMBER-E-003", "닉네임에 공백을 사용할 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER-E-004", "이미 사용중인 닉네임입니다."),

    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-010", "유효하지 않은 이메일 형식입니다."),

    INVALID_GENDER_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-020", "성별 값은 'M' 또는 'F'여야 합니다."),
    INVALID_HEIGHT_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-021", "키는 숫자(정수)로 입력해야 합니다."),
    INVALID_HEIGHT_RANGE(HttpStatus.BAD_REQUEST, "MEMBER-E-022", "키는 50 이상 300 이하로 입력해야 합니다."),
    INVALID_WEIGHT_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-023", "몸무게는 숫자(정수)로 입력해야 합니다."),
    INVALID_WEIGHT_RANGE(HttpStatus.BAD_REQUEST, "MEMBER-E-024", "몸무게는 20 이상 500 이하로 입력해야 합니다."),

    INVALID_STYLE_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-030", "유효하지 않은 스타일 타입입니다. 허용된 스타일 목록을 확인해주세요."),
    STYLE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "MEMBER-E-031", "스타일은 최대 2개까지 선택 가능합니다."),

    INVALID_NOTIFICATION_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-040", "알림 설정 값은 true 또는 false여야 합니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "MEMBER-E-050", "사용자를 찾을 수 없습니다."),
    NOT_FOUND_PENDING_MEMBER(HttpStatus.NOT_FOUND, "MEMBER-E-051", "회원가입이 완료되지 않은 사용자입니다."),
    NOT_FOUND_WITHDRAWN_MEMBER(HttpStatus.NOT_FOUND, "MEMBER-E-052", "탈퇴 후 재가입 유예 기간 중입니다. 14일 후에 다시 시도해주세요."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
