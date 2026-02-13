package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationSuccessCode implements ResponseCode {
    NOTIFICATION_LISTED(HttpStatus.OK, "NOTI-S-001", "알림 목록 조회를 완료했습니다."),
    NOTIFICATION_READ(HttpStatus.OK, "NOTI-S-002", "알림 읽음 처리를 완료했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
