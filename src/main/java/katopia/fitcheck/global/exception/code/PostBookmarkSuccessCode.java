package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostBookmarkSuccessCode implements ResponseCode {
    POST_BOOKMARKED(HttpStatus.CREATED, "POST-BOOKMARK-S-001", "게시글이 북마크되었습니다."),
    POST_UNBOOKMARKED(HttpStatus.NO_CONTENT, "POST-BOOKMARK-S-002", "게시글 북마크가 해제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
