package katopia.fitcheck.chat.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.chat.service.message.ChatReadStateService;
import katopia.fitcheck.global.security.SecuritySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class ChatReadStateSubscriptionListener {

    private final ChatReadStateService chatReadStateService;
    private final SecuritySupport securitySupport;
    private final ObjectMapper objectMapper;
    private final MessageChannel clientOutboundChannel;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String roomId = ChatPolicy.extractRoomIdFromReadStateTopic(destination);
        if (roomId == null) {
            return;
        }

        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();
        if (principal == null || sessionId == null || subscriptionId == null) {
            return;
        }

        Long memberId = securitySupport.requireMemberId(principal);
        ChatReadStateSnapshotResponse snapshot = chatReadStateService.getSnapshot(memberId, roomId);

        SimpMessageHeaderAccessor responseAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseAccessor.setSessionId(sessionId);
        responseAccessor.setSubscriptionId(subscriptionId);
        responseAccessor.setDestination(destination);
        responseAccessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        responseAccessor.setLeaveMutable(true);

        byte[] payload = serialize(snapshot, roomId, sessionId, subscriptionId);
        org.springframework.messaging.Message<byte[]> message =
                org.springframework.messaging.support.MessageBuilder.createMessage(payload, responseAccessor.getMessageHeaders());
        clientOutboundChannel.send(message);
    }

    private byte[] serialize(
            ChatReadStateSnapshotResponse snapshot,
            String roomId,
            String sessionId,
            String subscriptionId
    ) {
        try {
            return objectMapper.writeValueAsBytes(snapshot);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize chat read-state snapshot.", e);
        }
    }
}
