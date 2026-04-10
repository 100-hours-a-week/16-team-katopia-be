package katopia.fitcheck.redis.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.service.notification.NotificationSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisNotificationRealtimeSubscriber {

    private final NotificationSseService notificationSseService;
    private final ObjectMapper objectMapper;

    public void handleMessage(String message) {
        try {
            NotificationRealtimeEnvelope envelope = objectMapper.readValue(message, NotificationRealtimeEnvelope.class);
            if (envelope.recipientId() == null || envelope.payload() == null) {
                log.warn("[NOTIFICATION-REDIS] invalid envelope recipientId={}", envelope.recipientId());
                return;
            }
            notificationSseService.send(envelope.recipientId(), envelope.payload());
        } catch (JsonProcessingException e) {
            log.error("[NOTIFICATION-REDIS] subscribe handle failed message={}", message, e);
        }
    }
}
