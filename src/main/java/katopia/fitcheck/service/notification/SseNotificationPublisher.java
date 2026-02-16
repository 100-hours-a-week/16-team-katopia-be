package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseNotificationPublisher implements NotificationRealtimePublisher {

    private final NotificationSseService notificationSseService;

    @Override
    public void publish(Notification notification) {
        NotificationSummary summary = NotificationSummary.of(notification);
        notificationSseService.send(notification.getRecipient().getId(), summary);
    }
}
