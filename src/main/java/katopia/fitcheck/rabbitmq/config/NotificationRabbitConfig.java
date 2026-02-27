package katopia.fitcheck.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import katopia.fitcheck.rabbitmq.RabbitMqConstants;
import org.springframework.amqp.core.AcknowledgeMode;

@Configuration
public class NotificationRabbitConfig {

    @Bean
    public DirectExchange domainEventsExchange() {
        return new DirectExchange(RabbitMqConstants.DOMAIN_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationMetaQueue() {
        return QueueBuilder.durable(RabbitMqConstants.NOTIFICATION_TARGET_QUEUE).build();
    }

    @Bean
    public Queue notificationBatchQueue() {
        return QueueBuilder.durable(RabbitMqConstants.NOTIFICATION_BATCH_QUEUE).build();
    }

    @Bean
    public Binding notificationMetaBinding(Queue notificationMetaQueue, DirectExchange domainEventsExchange) {
        return BindingBuilder.bind(notificationMetaQueue)
                .to(domainEventsExchange)
                .with(RabbitMqConstants.NOTIFICATION_META_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBatchBinding(Queue notificationBatchQueue, DirectExchange domainEventsExchange) {
        return BindingBuilder.bind(notificationBatchQueue)
                .to(domainEventsExchange)
                .with(RabbitMqConstants.NOTIFICATION_BATCH_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(rabbitMessageConverter);
        template.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
