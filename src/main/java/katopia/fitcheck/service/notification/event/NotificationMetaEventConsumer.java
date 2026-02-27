package katopia.fitcheck.service.notification.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import katopia.fitcheck.messaging.event.MessageEvent;
import katopia.fitcheck.rabbitmq.RabbitMqConstants;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.repository.vote.VoteParticipationRepository;
import katopia.fitcheck.service.vote.VoteFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationMetaEventConsumer {

    private static final int BATCH_SIZE = 100;

    private final NotificationBatchEventPublisher batchEventPublisher;
    private final VoteParticipationRepository voteParticipationRepository;
    private final MemberFollowRepository memberFollowRepository;
    private final VoteFinder voteFinder;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            queues = RabbitMqConstants.NOTIFICATION_TARGET_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleMetaEvent(MessageEvent event, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            List<Long> targetIds = resolveTargets(event);
            if (!targetIds.isEmpty()) {
                NotificationPayload metaPayload = convertPayload(
                        event.getPayloadType(),
                        event.getPayload(),
                        NotificationPayload.class
                );
                publishBatches(event, metaPayload, targetIds);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.warn("Failed to process notification meta event. eventId={}", event.getEventId(), ex);
            channel.basicNack(deliveryTag, false, true);
        }
    }

    private List<Long> resolveTargets(MessageEvent event) {
        NotificationType notificationType = NotificationType.fromCode(event.getEventType());

        return switch (notificationType) {
            case POST_CREATED -> {
                if (event.getActorId() == null) {
                    yield Collections.emptyList();
                }
                yield memberFollowRepository.findFollowerIdsByFollowedId(event.getActorId());
            }
            case VOTE_CLOSED -> {
                if (event.getRefId() == null) {
                    yield Collections.emptyList();
                }
                List<Long> participantIds = voteParticipationRepository.findParticipantMemberIdsByVoteId(event.getRefId());
                Long authorId = voteFinder.findByIdOrThrow(event.getRefId()).getMember().getId();
                yield mergeTargets(participantIds, authorId);
            }
            default -> {
                List<Long> targets = event.getTargetIds();
                if (targets == null || targets.isEmpty()) {
                    yield Collections.emptyList();
                }
                yield targets;
            }
        };
    }

    private List<Long> mergeTargets(List<Long> participantIds, Long authorId) {
        if (participantIds == null || participantIds.isEmpty()) {
            return authorId == null ? Collections.emptyList() : Collections.singletonList(authorId);
        }
        if (authorId == null) {
            return participantIds;
        }
        java.util.LinkedHashSet<Long> merged = new java.util.LinkedHashSet<>(participantIds);
        merged.add(authorId);
        return new java.util.ArrayList<>(merged);
    }

    private void publishBatches(MessageEvent sourceEvent, NotificationPayload metaPayload, List<Long> targetIds) {
        List<List<Long>> chunks = chunk(targetIds, BATCH_SIZE);
        for (List<Long> chunk : chunks) {
            NotificationPayload payload = NotificationPayload.builder()
                    .actorNicknameSnapshot(metaPayload != null ? metaPayload.getActorNicknameSnapshot() : null)
                    .imageObjectKeySnapshot(metaPayload != null ? metaPayload.getImageObjectKeySnapshot() : null)
                    .messageArgs(metaPayload != null ? metaPayload.getMessageArgs() : null)
                    .build();
            MessageEvent batchEvent = MessageEvent.builder()
                    .eventId(MessageEvent.newEventId())
                    .eventType(sourceEvent.getEventType())
                    .occurredAt(MessageEvent.now())
                    .actorId(sourceEvent.getActorId())
                    .refId(sourceEvent.getRefId())
                    .payloadType(NotificationPayload.class.getSimpleName())
                    .payload(payload)
                    .targetIds(chunk)
                    .build();
            batchEventPublisher.publish(batchEvent);
        }
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

    private List<List<Long>> chunk(List<Long> targetIds, int size) {
        if (targetIds == null || targetIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<Long>> chunks = new ArrayList<>();
        for (int i = 0; i < targetIds.size(); i += size) {
            chunks.add(targetIds.subList(i, Math.min(i + size, targetIds.size())));
        }
        return chunks;
    }
}
