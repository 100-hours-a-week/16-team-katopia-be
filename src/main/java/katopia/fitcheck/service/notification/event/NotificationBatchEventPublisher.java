package katopia.fitcheck.service.notification.event;

import katopia.fitcheck.messaging.event.MessageEvent;
import katopia.fitcheck.rabbitmq.RabbitMqConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationBatchEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(MessageEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.DOMAIN_EVENTS_EXCHANGE,
                RabbitMqConstants.NOTIFICATION_BATCH_ROUTING_KEY,
                event
        );
    }
}
