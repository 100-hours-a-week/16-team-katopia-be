package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostSuccessCode implements ResponseCode {
    POST_CREATED(HttpStatus.CREATED, "POST-S-001", "게시글이 등록되었습니다."),
    POST_LISTED(HttpStatus.OK, "POST-S-002", "게시글 목록 조회를 완료되었습니다."),
    POST_FETCHED(HttpStatus.OK, "POST-S-003", "게시글 상세 조회를 완료되었습니다."),
    POST_UPDATED(HttpStatus.OK, "POST-S-004", "게시글이 수정되었습니다."),
    POST_DELETED(HttpStatus.NO_CONTENT, "POST-S-005", "게시글이 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
