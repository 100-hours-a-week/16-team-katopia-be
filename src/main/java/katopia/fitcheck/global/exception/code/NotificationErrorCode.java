package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ResponseCode {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI-E-001", "알림을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
