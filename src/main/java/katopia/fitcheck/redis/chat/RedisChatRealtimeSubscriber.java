package katopia.fitcheck.redis.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.ws.ChatReadStateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisChatRealtimeSubscriber {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    public void handleMessage(String message) {
        try {
            ChatRealtimeEnvelope envelope = objectMapper.readValue(message, ChatRealtimeEnvelope.class);
            if (envelope.destination() == null || envelope.payloadType() == null || envelope.payload() == null) {
                log.warn("[CHAT-REDIS] invalid envelope destination={}, payloadType={}",
                        envelope.destination(), envelope.payloadType());
                return;
            }

            Object payload = switch (envelope.payloadType()) {
                case MESSAGE -> objectMapper.treeToValue(envelope.payload(), ChatMessageResponse.class);
                case READ_STATE -> objectMapper.treeToValue(envelope.payload(), ChatReadStateResponse.class);
            };

            simpMessagingTemplate.convertAndSend(envelope.destination(), payload);
        } catch (JsonProcessingException e) {
            log.error("[CHAT-REDIS] subscribe handle failed message={}", message, e);
        }
    }
}
