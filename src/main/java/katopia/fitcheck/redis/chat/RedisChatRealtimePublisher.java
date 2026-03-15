package katopia.fitcheck.redis.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.ws.ChatPolicy;
import katopia.fitcheck.chat.ws.ChatReadStateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisChatRealtimePublisher {

    public static final String CHANNEL = "chat:realtime";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void publishMessage(ChatMessageResponse response) {
        publish(
                ChatPolicy.roomMessagesTopic(response.roomId()),
                ChatRealtimePayloadType.MESSAGE,
                response
        );
    }

    public void publishReadState(ChatReadStateResponse response) {
        publish(
                ChatPolicy.roomReadStateTopic(response.roomId()),
                ChatRealtimePayloadType.READ_STATE,
                response
        );
    }

    private void publish(String destination, ChatRealtimePayloadType payloadType, Object payload) {
        try {
            String message = objectMapper.writeValueAsString(
                    new ChatRealtimeEnvelope(
                            destination,
                            payloadType,
                            objectMapper.valueToTree(payload)
                    )
            );
            stringRedisTemplate.convertAndSend(CHANNEL, message);
        } catch (JsonProcessingException e) {
            log.error("[CHAT-REDIS] publish failed destination={}, payloadType={}", destination, payloadType, e);
            throw new IllegalStateException("Failed to publish chat realtime event.", e);
        }
    }
}
