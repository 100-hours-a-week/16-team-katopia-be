package katopia.fitcheck.service.sse;

import katopia.fitcheck.global.policy.Policy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractSseService<T> {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long memberId, List<T> initialPayloads) {
        SseEmitter emitter = new SseEmitter(Policy.SSE_TIMEOUT.toMillis());
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
        sendInitial(memberId, initialPayloads);
        return emitter;
    }

    public void send(Long memberId, T payload) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(toEvent(payload));
        } catch (IOException ex) {
            emitters.remove(memberId, emitter);
            emitter.completeWithError(ex);
            log.debug("SSE send failed for memberId={}", memberId, ex);
        }
    }

    protected abstract String eventName();

    protected SseEmitter.SseEventBuilder toEvent(T payload) {
        return SseEmitter.event()
                .name(eventName())
                .data(payload);
    }

    protected void sendInitial(Long memberId, List<T> initialPayloads) {
        if (initialPayloads == null || initialPayloads.isEmpty()) {
            return;
        }
        for (T payload : initialPayloads) {
            send(memberId, payload);
        }
    }
}
