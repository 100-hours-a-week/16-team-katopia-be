package katopia.fitcheck.service.notification;

import katopia.fitcheck.dto.notification.response.NotificationListResponse;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.global.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationQueryService queryService;
    private final NotificationSseService notificationSseService;

    @Transactional(readOnly = true)
    public NotificationListResponse getList(Long memberId, String size, String after) {
        return queryService.getList(memberId, size, after);
    }

    @Transactional
    public NotificationSummary markRead(Long memberId, Long notificationId) {
        return queryService.markRead(memberId, notificationId);
    }

    public SseEmitter connectStream(Long memberId, int unreadLimit) {
        List<NotificationSummary> unread = queryService.getLatestUnread(memberId, unreadLimit);
        return notificationSseService.connect(memberId, unread);
    }

    public SseEmitter connectStream(Long memberId) {
        return connectStream(memberId, Policy.SSE_UNREAD_LIMIT);
    }
}
