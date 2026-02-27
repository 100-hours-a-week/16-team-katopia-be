package katopia.fitcheck.service.notification;

import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.redis.sse.SseConnectionRegistry;
import katopia.fitcheck.redis.sse.SseDisconnectPublisher;
import katopia.fitcheck.service.sse.AbstractSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSseService extends AbstractSseService<NotificationSummary> {

    private static final String EVENT_NAME = "notification";

    private final SseConnectionRegistry connectionRegistry;
    private final SseDisconnectPublisher disconnectPublisher;

    @Override
    protected String eventName() {
        return EVENT_NAME;
    }

    @Override
    protected void onConnected(Long memberId, String connectionId, long connectedAt) {
        try {
            List<String> evicted = connectionRegistry.register(memberId, connectionId, connectedAt);
            for (String evictedConnectionId : evicted) {
                disconnectPublisher.publish(memberId, evictedConnectionId);
            }
        } catch (DataAccessException ex) {
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void onDisconnected(Long memberId, String connectionId) {
        try {
            connectionRegistry.remove(memberId, connectionId);
        } catch (DataAccessException ex) {
            log.debug("Failed to remove SSE connection from Redis. memberId={}, connectionId={}", memberId, connectionId);
        }
    }

    @Override
    protected SseEmitter.SseEventBuilder toEvent(NotificationSummary payload) {
        return SseEmitter.event()
                .name(EVENT_NAME)
                .id(String.valueOf(payload.id()))
                .data(payload);
    }

    public void sendHeartbeat() {
        super.sendHeartbeat();
    }
}
