package katopia.fitcheck.redis.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.domain.ChatMessageType;
import katopia.fitcheck.chat.ws.ChatReadStateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedisChatRealtimeSubscriberTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private RedisChatRealtimeSubscriber redisChatRealtimeSubscriber;

    @Test
    @DisplayName("TC-CHAT-REDIS-S-01 MESSAGE 이벤트를 수신하면 로컬 topic으로 fan-out")
    void tcChatRedisS01_messageEvent_fansOutToLocalTopic() throws Exception {
        redisChatRealtimeSubscriber = new RedisChatRealtimeSubscriber(simpMessagingTemplate, objectMapper);
        ChatMessageResponse payload = new ChatMessageResponse(
                "doc-1", 101L, "room-1", 7L, "핏체커", "profiles/7/profile.png",
                "안녕하세요", null, ChatMessageType.TEXT, Instant.parse("2026-03-15T12:34:56Z")
        );
        String message = objectMapper.writeValueAsString(new ChatRealtimeEnvelope(
                "/topic/chat/rooms/room-1/messages",
                ChatRealtimePayloadType.MESSAGE,
                objectMapper.valueToTree(payload)
        ));

        redisChatRealtimeSubscriber.handleMessage(message);

        verify(simpMessagingTemplate).convertAndSend("/topic/chat/rooms/room-1/messages", payload);
    }

    @Test
    @DisplayName("TC-CHAT-REDIS-S-02 READ_STATE 이벤트를 수신하면 로컬 topic으로 fan-out")
    void tcChatRedisS02_readStateEvent_fansOutToLocalTopic() throws Exception {
        redisChatRealtimeSubscriber = new RedisChatRealtimeSubscriber(simpMessagingTemplate, objectMapper);
        ChatReadStateResponse payload = new ChatReadStateResponse(
                "room-1", 7L, 101L, Instant.parse("2026-03-15T12:34:56Z")
        );
        String message = objectMapper.writeValueAsString(new ChatRealtimeEnvelope(
                "/topic/chat/rooms/room-1/read-state",
                ChatRealtimePayloadType.READ_STATE,
                objectMapper.valueToTree(payload)
        ));

        redisChatRealtimeSubscriber.handleMessage(message);

        verify(simpMessagingTemplate).convertAndSend("/topic/chat/rooms/room-1/read-state", payload);
    }
}
