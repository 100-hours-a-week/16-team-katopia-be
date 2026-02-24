package katopia.fitcheck.service.notification;

import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.redis.sse.SseConnectionRegistry;
import katopia.fitcheck.redis.sse.SseDisconnectPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSseConnectionCleanupScheduler {

    private final SseConnectionRegistry connectionRegistry;
    private final SseDisconnectPublisher disconnectPublisher;

    @Scheduled(fixedDelayString = "#{T(katopia.fitcheck.global.policy.Policy).SSE_CLEANUP_INTERVAL.toMillis()}")
    public void cleanupExpiredConnections() {
        long cutoff = System.currentTimeMillis() - Policy.SSE_TIMEOUT.toMillis();
        List<SseConnectionRegistry.SseExpiredConnection> expired;
        try {
            expired = connectionRegistry.findExpired(cutoff);
        } catch (DataAccessException ex) {
            log.debug("Failed to cleanup SSE connections.", ex);
            return;
        }
        if (expired.isEmpty()) {
            return;
        }
        Map<Long, List<String>> byMember = groupByMember(expired);
        for (Map.Entry<Long, List<String>> entry : byMember.entrySet()) {
            Long memberId = entry.getKey();
            List<String> connectionIds = entry.getValue();
            for (String connectionId : connectionIds) {
                disconnectPublisher.publish(memberId, connectionId);
            }
            connectionRegistry.removeExpired(memberId, connectionIds);
        }
    }

    private Map<Long, List<String>> groupByMember(
            List<SseConnectionRegistry.SseExpiredConnection> expired
    ) {
        Map<Long, List<String>> result = new HashMap<>();
        for (SseConnectionRegistry.SseExpiredConnection item : expired) {
            result.computeIfAbsent(item.memberId(), ignored -> new ArrayList<>())
                    .add(item.connectionId());
        }
        return result;
    }
}
