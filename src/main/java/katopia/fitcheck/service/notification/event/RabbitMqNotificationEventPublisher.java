package katopia.fitcheck.service.notification.event;

import katopia.fitcheck.messaging.event.MessageEvent;
import katopia.fitcheck.messaging.event.MessageEventPublisher;
import katopia.fitcheck.rabbitmq.RabbitMqConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqNotificationEventPublisher implements MessageEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(MessageEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.DOMAIN_EVENTS_EXCHANGE,
                RabbitMqConstants.NOTIFICATION_META_ROUTING_KEY,
                event
        );
    }
}
