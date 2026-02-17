package katopia.fitcheck.service.notification;

import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.service.sse.AbstractSseService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationSseService extends AbstractSseService<NotificationSummary> {

    private static final String EVENT_NAME = "notification";

    @Override
    protected String eventName() {
        return EVENT_NAME;
    }

    @Override
    protected SseEmitter.SseEventBuilder toEvent(NotificationSummary payload) {
        return SseEmitter.event()
                .name(EVENT_NAME)
                .id(String.valueOf(payload.id()))
                .data(payload);
    }
}
