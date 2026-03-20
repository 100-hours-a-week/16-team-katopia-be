package katopia.fitcheck.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSseHeartbeatScheduler {

    private final NotificationSseService notificationSseService;

    @Scheduled(fixedDelayString = "#{T(katopia.fitcheck.global.policy.Policy).SSE_HEARTBEAT_INTERVAL.toMillis()}")
    public void sendHeartbeat() {
        notificationSseService.sendHeartbeat();
    }
}
