package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.repository.notification.NotificationRepository;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.post.PostFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final PostFinder postFinder;
    private final VoteItemRepository voteItemRepository;
    private final MemberFinder memberFinder;
    private final NotificationRealtimePublisher realtimePublisher;

    @Transactional
    public void publishFollowNotification(Long actorId, Long recipientId) {
        if (actorId.equals(recipientId)) {
            return;
        }
        Member actor = memberFinder.findByIdOrThrow(actorId);
        Member recipient = memberFinder.findByIdOrThrow(recipientId);
        createNotification(actor, recipient, NotificationType.FOLLOW, Policy.FOLLOW_MESSAGE, actorId);
    }

    @Transactional
    public void publishPostLikeNotification(Long actorId, Long postId) {
        Long recipientId = postFinder.findMemberIdByPostIdOrThrow(postId);
        if (actorId.equals(recipientId)) {
            return;
        }
        Member actor = memberFinder.findByIdOrThrow(actorId);
        Member recipient = memberFinder.findByIdOrThrow(recipientId);
        createNotification(actor, recipient, NotificationType.POST_LIKE, Policy.POST_LIKE_MESSAGE, postId);
    }

    @Transactional
    public void publishPostCommentNotification(Long actorId, Long postId) {
        Long recipientId = postFinder.findMemberIdByPostIdOrThrow(postId);
        if (actorId.equals(recipientId)) {
            return;
        }
        Member actor = memberFinder.findByIdOrThrow(actorId);
        Member recipient = memberFinder.findByIdOrThrow(recipientId);
        createNotification(actor, recipient, NotificationType.POST_COMMENT, Policy.POST_COMMENT_MESSAGE, postId);
    }

    @Transactional
    public void publishVoteClosedNotification(Long recipientId, Long voteId) {
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
        if (recipient.isEnableRealtimeNotification()) {
            realtimePublisher.publish(notification);
        }
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
}
