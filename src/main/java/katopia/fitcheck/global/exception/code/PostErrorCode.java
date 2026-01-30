package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ResponseCode {
    CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "POST-E-000", "본문 내용은 필수 입력 항목입니다."),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "POST-E-001", "본문 내용은 최대 200자 입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-E-002", "게시글을 찾을 수 없습니다."),

    IMAGE_COUNT_INVALID(HttpStatus.BAD_REQUEST, "POST-E-010", "이미지는 최소 1장 이상, 최대 3장까지 등록 가능합니다."),

    TAG_LENGTH_INVALID(HttpStatus.BAD_REQUEST, "POST-E-020", "태그는 최소 1자, 최대 20자 등록 가능합니다."),
    TAG_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "POST-E-021", "태그는 최대 10개까지 등록 가능합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
