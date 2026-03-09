package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseNotificationPublisher implements NotificationRealtimePublisher {

    private final NotificationSseService notificationSseService;

    @Override
    @Async("notificationSendExecutor")
    public void publish(Notification notification) {
        try {
            NotificationSummary summary = NotificationSummary.of(notification);
            notificationSseService.send(notification.getRecipient().getId(), summary);
        } catch (Exception ex) {
            log.warn("Failed to send realtime notification. notificationId={}", notification.getId(), ex);
        }
    }
}
