package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.dto.notification.response.NotificationListResponse;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.NotificationErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

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

    @Transactional
    public void createFollow(Member actor, Member recipient) {
        createNotification(actor, recipient, NotificationType.FOLLOW, Policy.FOLLOW_MESSAGE, actor.getId());
    }

    @Transactional
    public void createPostLike(Member actor, Member recipient, Long postId) {
        createNotification(actor, recipient, NotificationType.POST_LIKE, Policy.POST_LIKE_MESSAGE, postId);
    }

    @Transactional
    public void createPostComment(Member actor, Member recipient, Long postId) {
        createNotification(actor, recipient, NotificationType.POST_COMMENT, Policy.POST_COMMENT_MESSAGE, postId);
    }

    @Transactional
    public void createVoteClosed(Member recipient, Long voteId) {
        createNotification(null, recipient, NotificationType.VOTE_CLOSED, Policy.VOTE_CLOSED_MESSAGE, voteId);
    }

    private void createNotification(
            Member actor,
            Member recipient,
            NotificationType type,
            String messageFormat,
            Long referenceId
    ) {
        if (actor != null && actor.getId().equals(recipient.getId())) {
            return;
        }
        String message = actor == null ? messageFormat : String.format(messageFormat, actor.getNickname());
        Notification notification = Notification.of(recipient, actor, type, message, referenceId);
        notificationRepository.save(notification);
        // TODO: Redis/RabbitMQ 연동으로 실시간 전송/재시도 큐 처리
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
