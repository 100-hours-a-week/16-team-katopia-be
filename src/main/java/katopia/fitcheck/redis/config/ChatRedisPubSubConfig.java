package katopia.fitcheck.redis.config;

import katopia.fitcheck.redis.chat.RedisChatRealtimePublisher;
import katopia.fitcheck.redis.chat.RedisChatRealtimeSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class ChatRedisPubSubConfig {

    @Bean
    public MessageListenerAdapter chatRealtimeListener(RedisChatRealtimeSubscriber subscriber) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "handleMessage");
        adapter.setSerializer(new StringRedisSerializer());
        return adapter;
    }

    @Bean
    public RedisMessageListenerContainer chatRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter chatRealtimeListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
                chatRealtimeListener,
                new ChannelTopic(RedisChatRealtimePublisher.CHANNEL)
        );
        return container;
    }
}
