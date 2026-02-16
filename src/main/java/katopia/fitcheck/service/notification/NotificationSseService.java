package katopia.fitcheck.service.notification;

import katopia.fitcheck.dto.notification.response.NotificationSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationSseService {

    private static final long TIMEOUT_MS = 60L * 60L * 1000L;
    private static final String EVENT_NAME = "notification";

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long memberId, List<NotificationSummary> unreadSummaries) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        SseEmitter existing = emitters.put(memberId, emitter);
        if (existing != null) {
            existing.complete();
        }
        emitter.onCompletion(() -> emitters.remove(memberId, emitter));
        emitter.onTimeout(() -> {
            emitters.remove(memberId, emitter);
            emitter.complete();
        });
        emitter.onError(ex -> emitters.remove(memberId, emitter));
        sendInitialUnread(memberId, unreadSummaries);
        return emitter;
    }

    public void send(Long memberId, NotificationSummary summary) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .name(EVENT_NAME)
                    .id(String.valueOf(summary.id()))
                    .data(summary));
        } catch (IOException ex) {
            emitters.remove(memberId, emitter);
            emitter.completeWithError(ex);
            log.debug("SSE send failed for memberId={}", memberId, ex);
        }
    }

    private void sendInitialUnread(Long memberId, List<NotificationSummary> unreadSummaries) {
        if (unreadSummaries == null || unreadSummaries.isEmpty()) {
            return;
        }
        for (NotificationSummary summary : unreadSummaries) {
            send(memberId, summary);
        }
    }
}
