package katopia.fitcheck.domain.notification;

import katopia.fitcheck.global.policy.Policy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    FOLLOW(Policy.NOTIFICATION_TYPE_FOLLOW),
    POST_CREATED(Policy.NOTIFICATION_TYPE_POST_CREATED),
    POST_LIKE(Policy.NOTIFICATION_TYPE_POST_LIKE),
    POST_COMMENT(Policy.NOTIFICATION_TYPE_POST_COMMENT),
    VOTE_CLOSED(Policy.NOTIFICATION_TYPE_VOTE_CLOSED);

    private final String code;

    public static NotificationType fromCode(String code) {
        return NotificationType.valueOf(code);
    }
}
