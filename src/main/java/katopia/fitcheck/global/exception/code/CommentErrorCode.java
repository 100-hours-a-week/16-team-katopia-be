package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ResponseCode {
    CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "COMMENT-E-001", "본문 내용은 필수 입력 항목입니다."),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "COMMENT-E-002", "본문 내용은 최대 200자 입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-E-003", "댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
