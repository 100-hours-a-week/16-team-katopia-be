package katopia.fitcheck.redis.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseDisconnectPublisher {

    public static final String CHANNEL = "sse:notification:disconnect";

    private final StringRedisTemplate stringRedisTemplate;

    public void publish(Long memberId, String connectionId) {
        String message = SseDisconnectMessageConverter.encode(memberId, connectionId);
        stringRedisTemplate.convertAndSend(CHANNEL, message);
    }
}
