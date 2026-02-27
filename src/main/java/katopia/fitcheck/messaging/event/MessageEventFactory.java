package katopia.fitcheck.messaging.event;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.service.notification.event.NotificationPayload;
import java.util.List;
public final class MessageEventFactory {

    private MessageEventFactory() { }

    public static MessageEvent followCreated(Member actor, Long recipientId) {
        NotificationPayload payload = NotificationPayload.builder()
                .actorNicknameSnapshot(actor.getNickname())
                .imageObjectKeySnapshot(actor.getProfileImageObjectKey())
                .build();
        return baseEvent(
                NotificationType.FOLLOW.getCode(),
                actor.getId(),
                List.of(recipientId),
                actor.getId(),
                payload
        );
    }

    public static MessageEvent postLiked(Member actor, Long recipientId, Long postId, String imageObjectKeySnapshot) {
        NotificationPayload payload = NotificationPayload.builder()
                .actorNicknameSnapshot(actor.getNickname())
                .imageObjectKeySnapshot(imageObjectKeySnapshot)
                .build();
        return baseEvent(
                NotificationType.POST_LIKE.getCode(),
                actor.getId(),
                List.of(recipientId),
                postId,
                payload
        );
    }

    public static MessageEvent postCreated(Member actor, Long postId, String imageObjectKeySnapshot) {
        NotificationPayload payload = NotificationPayload.builder()
                .actorNicknameSnapshot(actor.getNickname())
                .imageObjectKeySnapshot(imageObjectKeySnapshot)
                .build();
        return baseEvent(NotificationType.POST_CREATED.getCode(), actor.getId(), null, postId, payload);
    }

    public static MessageEvent commentCreated(
            Member actor,
            Long recipientId,
            Long postId,
            Long commentId,
            String imageObjectKeySnapshot
    ) {
        NotificationPayload payload = NotificationPayload.builder()
                .actorNicknameSnapshot(actor.getNickname())
                .imageObjectKeySnapshot(imageObjectKeySnapshot)
                .messageArgs(new String[]{String.valueOf(commentId)})
                .build();
        return baseEvent(
                NotificationType.POST_COMMENT.getCode(),
                actor.getId(),
                List.of(recipientId),
                postId,
                payload
        );
    }

    public static MessageEvent voteClosed(Long voteId, String voteTitle, String imageObjectKeySnapshot) {
        NotificationPayload payload = NotificationPayload.builder()
                .imageObjectKeySnapshot(imageObjectKeySnapshot)
                .messageArgs(new String[]{voteTitle})
                .build();
        return baseEvent(NotificationType.VOTE_CLOSED.getCode(), null, null, voteId, payload);
    }

    private static MessageEvent baseEvent(
            String eventType,
            Long actorId,
            List<Long> targetIds,
            Long refId,
            Object payload
    ) {
        return MessageEvent.builder()
                .eventId(MessageEvent.newEventId())
                .eventType(eventType)
                .occurredAt(MessageEvent.now())
                .payloadType(NotificationPayload.class.getSimpleName())
                .payload(payload)
                .actorId(actorId)
                .targetIds(targetIds)
                .refId(refId)
                .build();
    }
}
