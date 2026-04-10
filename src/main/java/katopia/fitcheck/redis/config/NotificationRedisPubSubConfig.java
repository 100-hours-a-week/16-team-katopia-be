package katopia.fitcheck.redis.config;

import katopia.fitcheck.redis.notification.RedisNotificationRealtimePublisher;
import katopia.fitcheck.redis.notification.RedisNotificationRealtimeSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class NotificationRedisPubSubConfig {

    @Bean
    public MessageListenerAdapter notificationRealtimeListener(RedisNotificationRealtimeSubscriber subscriber) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "handleMessage");
        adapter.setSerializer(new StringRedisSerializer());
        return adapter;
    }

    @Bean
    public RedisMessageListenerContainer notificationRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter notificationRealtimeListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
                notificationRealtimeListener,
                new ChannelTopic(RedisNotificationRealtimePublisher.CHANNEL)
        );
        return container;
    }
}
