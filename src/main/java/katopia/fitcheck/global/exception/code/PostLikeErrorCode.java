package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostLikeErrorCode implements ResponseCode {
    ALREADY_LIKED(HttpStatus.CONFLICT, "POST-LIKE-E-001", "이미 좋아요를 누른 게시글입니다."),
    NOT_FOUND_LIKE(HttpStatus.NOT_FOUND, "POST-LIKE-E-002", "취소할 좋아요 기록이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
