package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentSuccessCode implements ResponseCode {
    COMMENT_CREATED(HttpStatus.CREATED, "COMMENT-S-001", "댓글이 등록되었습니다."),
    COMMENT_LISTED(HttpStatus.OK, "COMMENT-S-002", "댓글 목록 조회를 완료되었습니다."),
    COMMENT_UPDATED(HttpStatus.OK, "COMMENT-S-003", "댓글이 수정되었습니다."),
    COMMENT_DELETED(HttpStatus.NO_CONTENT, "COMMENT-S-004", "댓글이 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
