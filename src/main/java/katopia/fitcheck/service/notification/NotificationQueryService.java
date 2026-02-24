package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.dto.notification.response.NotificationListResponse;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.NotificationErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getList(Long memberId, String sizeValue, String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Notification> notifications = loadNotifications(memberId, size, after);

        List<NotificationSummary> summaries = notifications.stream()
                .map(NotificationSummary::of)
                .toList();

        String nextCursor = null;
        if (!notifications.isEmpty() && notifications.size() == size) {
            Notification last = notifications.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.getCreatedAt(), last.getId());
        }

        return NotificationListResponse.of(summaries, nextCursor);
    }

    @Transactional
    public NotificationSummary markRead(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndRecipientId(notificationId, memberId)
                .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
        if (notification.getReadAt() != null) {
            throw new BusinessException(NotificationErrorCode.NOTIFICATION_ALREADY_READ);
        }
        notification.markRead(LocalDateTime.now());
        return NotificationSummary.of(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationSummary> getLatestUnread(Long memberId, int size) {
        List<Notification> notifications = notificationRepository.findLatestUnreadByRecipientId(
                memberId,
                PageRequest.of(0, size)
        );
        return notifications.stream()
                .map(NotificationSummary::of)
                .toList();
    }

    private List<Notification> loadNotifications(Long memberId, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return notificationRepository.findLatestByRecipientId(memberId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return notificationRepository.findPageAfter(memberId, cursor.createdAt(), cursor.id(), pageRequest);
    }
}
