package katopia.fitcheck.redis.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisNotificationRealtimePublisher {

    public static final String CHANNEL = "notification:realtime";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Long recipientId, NotificationSummary payload) {
        try {
            String message = objectMapper.writeValueAsString(
                    new NotificationRealtimeEnvelope(recipientId, payload)
            );
            stringRedisTemplate.convertAndSend(CHANNEL, message);
        } catch (JsonProcessingException e) {
            log.error("[NOTIFICATION-REDIS] publish failed recipientId={}", recipientId, e);
            throw new IllegalStateException("Failed to publish notification realtime event.", e);
        }
    }
}
