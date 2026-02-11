package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostBookmarkErrorCode implements ResponseCode {
    ALREADY_BOOKMARKED(HttpStatus.CONFLICT, "POST-BOOKMARK-E-001", "이미 북마크한 게시글입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-BOOKMARK-E-002", "북마크한 게시글이 아닙니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
