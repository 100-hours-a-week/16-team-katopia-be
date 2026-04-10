package katopia.fitcheck.redis.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.service.notification.NotificationSseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RedisNotificationRealtimeSubscriberTest {

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-06 Redis 알림 이벤트 수신 시 recipient 기준으로 SSE 전송")
    void tcNotificationSseS06_handleMessage_sendsToRecipient() throws Exception {
        NotificationSseService notificationSseService = mock(NotificationSseService.class);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        RedisNotificationRealtimeSubscriber subscriber = new RedisNotificationRealtimeSubscriber(notificationSseService, objectMapper);

        NotificationSummary payload = NotificationSummary.builder().id(1L).build();
        String message = objectMapper.writeValueAsString(new NotificationRealtimeEnvelope(7L, payload));

        subscriber.handleMessage(message);

        verify(notificationSseService).send(7L, payload);
    }
}
