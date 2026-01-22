package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostLikeSuccessCode implements ResponseCode {
    POST_LIKED(HttpStatus.CREATED, "POST-LIKE-S-001", "해당 게시글에 좋아요를 남겼습니다."),
    POST_UNLIKED(HttpStatus.NO_CONTENT, "POST-LIKE-S-002", "게시글 좋아요가 취소되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
