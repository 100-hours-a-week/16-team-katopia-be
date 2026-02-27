package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.messaging.event.MessageEventFactory;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.messaging.event.MessageEvent;
import katopia.fitcheck.messaging.event.MessageEventPublisher;
import katopia.fitcheck.service.notification.event.NotificationBatchEventPublisher;
import katopia.fitcheck.service.post.PostFinder;
import katopia.fitcheck.service.vote.VoteFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final PostFinder postFinder;
    private final VoteFinder voteFinder;
    private final MemberFinder memberFinder;
    private final MessageEventPublisher eventPublisher;
    private final NotificationBatchEventPublisher batchEventPublisher;

    @Transactional
    public void publishFollowNotification(Long actorId, Long recipientId) {
        Member actor = memberFinder.findByIdOrThrow(actorId);
        MessageEvent event = MessageEventFactory.followCreated(actor, recipientId);
        batchEventPublisher.publish(event);
    }

    @Transactional
    public void publishPostLikeNotification(Long actorId, Long postId) {
        Long recipientId = postFinder.findMemberIdByPostIdOrThrow(postId);
        Member actor = memberFinder.findByIdOrThrow(actorId);
        String imageObjectKeySnapshot = resolveImageObjectKeySnapshot(NotificationType.POST_LIKE, actor, postId);
        MessageEvent event = MessageEventFactory.postLiked(actor, recipientId, postId, imageObjectKeySnapshot);
        batchEventPublisher.publish(event);
    }

    @Transactional
    public void publishPostCreatedNotification(Long actorId, Long postId) {
        Member actor = memberFinder.findByIdOrThrow(actorId);
        String imageObjectKeySnapshot = resolveImageObjectKeySnapshot(NotificationType.POST_CREATED, actor, postId);
        MessageEvent event = MessageEventFactory.postCreated(actor, postId, imageObjectKeySnapshot);
        eventPublisher.publish(event);
    }

    @Transactional
    public void publishPostCommentNotification(Long actorId, Long postId, Long commentId) {
        Long recipientId = postFinder.findMemberIdByPostIdOrThrow(postId);

        // 댓글 작성자가 게시글 작성자와 일치할 경우 알림 미발행
        if (actorId.equals(recipientId)) {
            return;
        }
        Member actor = memberFinder.findByIdOrThrow(actorId);
        String imageObjectKeySnapshot = resolveImageObjectKeySnapshot(NotificationType.POST_COMMENT, actor, postId);
        MessageEvent event = MessageEventFactory.commentCreated(actor, recipientId, postId, commentId, imageObjectKeySnapshot);
        batchEventPublisher.publish(event);
    }

    @Transactional
    public void publishVoteClosedNotification(Long voteId) {
        String imageObjectKeySnapshot = resolveImageObjectKeySnapshot(NotificationType.VOTE_CLOSED, null, voteId);
        String voteTitle = voteFinder.findByIdOrThrow(voteId).getTitle();
        MessageEvent event = MessageEventFactory.voteClosed(voteId, voteTitle, imageObjectKeySnapshot);
        eventPublisher.publish(event);
    }

    private String resolveImageObjectKeySnapshot(NotificationType type, Member actor, Long refId) {
        return switch (type) {
            case FOLLOW -> actor.getProfileImageObjectKey();
            case POST_CREATED, POST_LIKE, POST_COMMENT -> postFinder.findThumbnailImageObjectKey(refId);
            case VOTE_CLOSED -> voteFinder.findThumbnailImageObjectKey(refId);
        };
    }
}
