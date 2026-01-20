package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ResponseCode {
    REQUIRED_VALUE(HttpStatus.BAD_REQUEST, "COMMON-E-001", "%s을 입력해주세요."),
    INVALID_ID_FORMAT(HttpStatus.BAD_REQUEST, "COMMON-E-002", "유효하지 않은 식별자 형식입니다."),
    INVALID_PAGE_SIZE_FORMAT(HttpStatus.BAD_REQUEST, "COMMON-E-003", "페이지 크기(size)는 숫자 형식이어야 합니다."),
    INVALID_SEARCH_QUERY_LEN(HttpStatus.BAD_REQUEST, "COMMON-E-004", "검색어는 최소 2자, 최대 100자 이내 이어야 합니다."),
    API_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-E-005", "요청한 API 경로를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-E-006", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-E-999", "시스템 오류로 작업을 수행하지 못했습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    public String getFormattedMessage(Object... args) {
        return String.format(this.message, args);
    }
}
