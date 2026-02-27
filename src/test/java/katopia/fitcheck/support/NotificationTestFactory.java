package katopia.fitcheck.support;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public final class NotificationTestFactory {

    private NotificationTestFactory() {
    }

    public static Notification notification(Long id,
                                            Member recipient,
                                            Member actor,
                                            NotificationType type,
                                            String message,
                                            Long refId,
                                            String imageObjectKeySnapshot,
                                            LocalDateTime createdAt,
                                            LocalDateTime readAt) {
        Notification notification = Notification.of(recipient, actor, type, message, refId, imageObjectKeySnapshot);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", createdAt);
        ReflectionTestUtils.setField(notification, "readAt", readAt);
        return notification;
    }
}
