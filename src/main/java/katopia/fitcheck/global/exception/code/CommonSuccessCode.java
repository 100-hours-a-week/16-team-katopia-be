package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonSuccessCode implements ResponseCode {
    SEARCH_COMPLETED(HttpStatus.OK, "COMMON-S-001", "검색을 완료했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
