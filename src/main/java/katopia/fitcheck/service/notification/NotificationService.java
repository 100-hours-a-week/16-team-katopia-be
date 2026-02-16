package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.dto.notification.response.NotificationListResponse;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.NotificationErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.repository.notification.NotificationRepository;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.post.PostFinder;
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
    private final PostFinder postFinder;
    private final VoteItemRepository voteItemRepository;
    private final MemberFinder memberFinder;
    private final NotificationRealtimePublisher realtimePublisher;

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

    @Transactional
    public void createFollow(Long actorId, Long recipientId) {
        if (actorId.equals(recipientId)) {
            return;
        }
        Member actor = memberFinder.findByIdOrThrow(actorId);
        Member recipient = memberFinder.findByIdOrThrow(recipientId);
        createNotification(actor, recipient, NotificationType.FOLLOW, Policy.FOLLOW_MESSAGE, actorId);
    }

    @Transactional
    public void createPostLike(Long actorId, Long postId) {
        Long recipientId = postFinder.findMemberIdByPostIdOrThrow(postId);
        if (actorId.equals(recipientId)) {
            return;
        }
        Member actor = memberFinder.findByIdOrThrow(actorId);
        Member recipient = memberFinder.findByIdOrThrow(recipientId);
        createNotification(actor, recipient, NotificationType.POST_LIKE, Policy.POST_LIKE_MESSAGE, postId);
    }

    @Transactional
    public void createPostComment(Long actorId, Long postId) {
        Long recipientId = postFinder.findMemberIdByPostIdOrThrow(postId);
        if (actorId.equals(recipientId)) {
            return;
        }
        Member actor = memberFinder.findByIdOrThrow(actorId);
        Member recipient = memberFinder.findByIdOrThrow(recipientId);
        createNotification(actor, recipient, NotificationType.POST_COMMENT, Policy.POST_COMMENT_MESSAGE, postId);
    }

    @Transactional
    public void createVoteClosed(Long recipientId, Long voteId) {
        Member recipient = memberFinder.findByIdOrThrow(recipientId);
        createNotification(null, recipient, NotificationType.VOTE_CLOSED, Policy.VOTE_CLOSED_MESSAGE, voteId);
    }

    private void createNotification(
            Member actor,
            Member recipient,
            NotificationType type,
            String messageFormat,
            Long refId
    ) {
        if (actor != null && actor.getId().equals(recipient.getId())) {
            return;
        }
        String message = actor == null ? messageFormat : String.format(messageFormat, actor.getNickname());
        String imageObjectKeySnapshot = resolveImageObjectKeySnapshot(type, actor, refId);
        Notification notification = Notification.of(recipient, actor, type, message, refId, imageObjectKeySnapshot);
        notificationRepository.save(notification);
        realtimePublisher.publish(notification);
        // TODO: Redis/RabbitMQ 연동으로 실시간 전송/재시도 큐 처리
    }

    private String resolveImageObjectKeySnapshot(NotificationType type, Member actor, Long refId) {
        return switch (type) {
            case FOLLOW -> actor != null ? actor.getProfileImageObjectKey() : null;
            case POST_LIKE, POST_COMMENT -> resolvePostImageObjectKey(refId);
            case VOTE_CLOSED -> resolveVoteImageObjectKey(refId);
        };
    }

    private String resolvePostImageObjectKey(Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);
        if (post.getImages() == null || post.getImages().isEmpty()) {
            return null;
        }
        return post.getImages().getFirst().getImageObjectKey();
    }

    private String resolveVoteImageObjectKey(Long voteId) {
        return voteItemRepository.findFirstByVoteIdOrderBySortOrderAsc(voteId)
                .map(VoteItem::getImageObjectKey)
                .orElse(null);
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
