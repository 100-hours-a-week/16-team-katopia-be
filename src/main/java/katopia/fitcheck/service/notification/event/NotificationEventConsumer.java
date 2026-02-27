package katopia.fitcheck.service.notification.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import katopia.fitcheck.messaging.event.MessageEvent;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.rabbitmq.RabbitMqConstants;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.repository.notification.NotificationRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.notification.NotificationRealtimePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationRepository notificationRepository;
    private final MemberFinder memberFinder;
    private final NotificationRealtimePublisher realtimePublisher;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            queues = RabbitMqConstants.NOTIFICATION_BATCH_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleNotificationEvent(MessageEvent event, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            NotificationPayload payload = convertPayload(event.getPayloadType(), event.getPayload(), NotificationPayload.class);
            if (event.getTargetIds() == null || event.getTargetIds().isEmpty()) {
                channel.basicAck(deliveryTag, false);
                return;
            }
            for (Long targetId : event.getTargetIds()) {
                Notification notification = createNotification(event, payload, targetId);
                notificationRepository.save(notification);
                if (notification.getRecipient().isEnableRealtimeNotification()) {
                    realtimePublisher.publish(notification);
                }
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.warn("Failed to process notification event. eventId={}", event.getEventId(), ex);
            channel.basicNack(deliveryTag, false, true);
        }
        // TODO: fan-out(투표 종료/게시글 작성) 규모 커지면 전송을 @Async로 분리 검토
    }

    private Notification createNotification(MessageEvent event, NotificationPayload payload, Long targetId) {
        NotificationType notificationType = NotificationType.fromCode(event.getEventType());

        Member recipient = memberFinder.findByIdOrThrow(targetId);
        Member actor = event.getActorId() == null ? null : memberFinder.findByIdOrThrow(event.getActorId());
        NotificationPayloadResult payloadResult = resolvePayload(notificationType, payload);


        String message = payloadResult.message();
        String imageObjectKeySnapshot = payloadResult.imageObjectKeySnapshot();
        return Notification.of(recipient, actor, notificationType, message, event.getRefId(), imageObjectKeySnapshot);
    }

    private NotificationPayloadResult resolvePayload(
            NotificationType notificationType,
            NotificationPayload data
    ) {
        return switch (notificationType) {
            case FOLLOW -> {
                String nickname = data != null ? data.getActorNicknameSnapshot() : Policy.SYSTEM;
                String imageKey = data != null ? data.getImageObjectKeySnapshot() : null;
                String message = String.format(Policy.FOLLOW_MESSAGE, nickname);
                yield new NotificationPayloadResult(message, imageKey);
            }
            case POST_CREATED -> {
                String nickname = data != null ? data.getActorNicknameSnapshot() : Policy.SYSTEM;
                String imageKey = data != null ? data.getImageObjectKeySnapshot() : null;
                String message = String.format(Policy.POST_CREATED_MESSAGE, nickname);
                yield new NotificationPayloadResult(message, imageKey);
            }
            case POST_LIKE -> {
                String nickname = data != null ? data.getActorNicknameSnapshot() : Policy.SYSTEM;
                String imageKey = data != null ? data.getImageObjectKeySnapshot() : null;
                String message = String.format(Policy.POST_LIKE_MESSAGE, nickname);
                yield new NotificationPayloadResult(message, imageKey);
            }
            case POST_COMMENT -> {
                String nickname = data != null ? data.getActorNicknameSnapshot() : Policy.SYSTEM;
                String imageKey = data != null ? data.getImageObjectKeySnapshot() : null;
                String message = String.format(Policy.POST_COMMENT_MESSAGE, nickname);
                yield new NotificationPayloadResult(message, imageKey);
            }
            case VOTE_CLOSED -> {
                String imageKey = data != null ? data.getImageObjectKeySnapshot() : null;
                String voteTitle = resolveMessageArg(data, 0);
                String message = String.format(Policy.VOTE_CLOSED_MESSAGE, voteTitle);
                yield new NotificationPayloadResult(message, imageKey);
            }
        };
    }

    private <T> T convertPayload(String payloadType, Object payload, Class<T> targetType) {
        if (payload == null) {
            return null;
        }
        if (!targetType.getSimpleName().equals(payloadType)) {
            log.warn("Unexpected payloadType. expected={}, actual={}", targetType.getSimpleName(), payloadType);
        }
        return objectMapper.convertValue(payload, targetType);
    }

    private record NotificationPayloadResult(String message, String imageObjectKeySnapshot) { }

    private String resolveMessageArg(NotificationPayload payload, int index) {
        if (payload == null || payload.getMessageArgs() == null || payload.getMessageArgs().length <= index) {
            return "";
        }
        String value = payload.getMessageArgs()[index];
        return value == null ? "" : value;
    }
}
