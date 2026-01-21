package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ResponseCode {
    INVALID_NICKNAME_LEN(HttpStatus.BAD_REQUEST, "MEMBER-E-001", "닉네임은 최대 20자까지 입력 가능합니다."),
    INVALID_NICKNAME_CHARACTERS(HttpStatus.BAD_REQUEST, "MEMBER-E-002", ".(온점), _(아래 밑줄) 외의 특수문자는 포함할 수 없습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-003", "유효하지 않은 이메일 형식입니다."),
    INVALID_GENDER_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-005", "성별 값은 'M' 또는 'F'여야 합니다."),
    INVALID_BODY_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-006", "키와 몸무게는 숫자(정수)로 입력해야 합니다."),
    INVALID_BODY_RANGE(HttpStatus.BAD_REQUEST, "MEMBER-E-007", "신체 정보 수치가 유효 범위를 벗어났습니다."),
    INVALID_STYLE_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-008", "유효하지 않은 스타일 타입입니다. 허용된 스타일 목록을 확인해주세요."),
    INVALID_NOTIFICATION_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER-E-009", "알림 설정 값은 true 또는 false여야 합니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "MEMBER-E-010", "사용자를 찾을 수 없습니다."),
    ALREADY_WITHDRAWN_MEMBER(HttpStatus.FORBIDDEN, "MEMBER-E-011", "이미 탈퇴 처리가 완료된 계정입니다."),
    WAITING_REJOIN_PERIOD(HttpStatus.FORBIDDEN, "MEMBER-E-012", "탈퇴 후 재가입 유예 기간 중입니다. 14일 후에 다시 시도해주세요."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER-E-013", "이미 사용중인 닉네임입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
