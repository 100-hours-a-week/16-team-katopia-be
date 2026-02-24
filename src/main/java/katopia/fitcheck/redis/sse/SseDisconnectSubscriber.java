package katopia.fitcheck.redis.sse;

import katopia.fitcheck.service.notification.NotificationSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseDisconnectSubscriber {

    private final NotificationSseService notificationSseService;

    public void handleMessage(String message) {
        SseDisconnectMessageConverter.decode(message)
                .ifPresentOrElse(
                        parsed -> notificationSseService.disconnect(parsed.connectionId()),
                        () -> log.debug("Invalid SSE disconnect message: {}", message)
                );
    }
}
