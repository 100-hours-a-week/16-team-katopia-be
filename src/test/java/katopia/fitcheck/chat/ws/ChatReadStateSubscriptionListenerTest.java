package katopia.fitcheck.chat.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import katopia.fitcheck.chat.service.message.ChatReadStateService;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatReadStateSubscriptionListenerTest {

    @Mock
    private ChatReadStateService chatReadStateService;

    @Mock
    private SecuritySupport securitySupport;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageChannel clientOutboundChannel;

    @InjectMocks
    private ChatReadStateSubscriptionListener chatReadStateSubscriptionListener;

    @Test
    @DisplayName("TC-CHAT-WS-S-04 read-state topic 구독 시 스냅샷을 해당 세션에 1회 전송")
    void tcChatWsS04_subscribeReadStateTopic_sendsSnapshot() {
        MemberPrincipal principal = new MemberPrincipal(7L);
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/chat/rooms/room-1/read-state");
        accessor.setSubscriptionId("sub-read-state");
        accessor.setSessionId("session-1");
        accessor.setUser(principal);
        Message<byte[]> message = org.springframework.messaging.support.MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );
        ApplicationEvent event = new SessionSubscribeEvent(this, message);
        ChatReadStateSnapshotResponse snapshot = ChatReadStateSnapshotResponse.of(
                "room-1",
                List.of(new ChatParticipantReadState(7L, 101L), new ChatParticipantReadState(8L, 99L))
        );
        byte[] payload = "{\"roomId\":\"room-1\"}".getBytes();

        when(securitySupport.requireMemberId((Principal) principal)).thenReturn(7L);
        when(chatReadStateService.getSnapshot(7L, "room-1")).thenReturn(snapshot);
        when(objectMapper.writeValueAsBytes(snapshot)).thenReturn(payload);

        chatReadStateSubscriptionListener.onSubscribe((SessionSubscribeEvent) event);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(clientOutboundChannel).send(captor.capture());

        Message<?> outbound = captor.getValue();
        StompHeaderAccessor outboundAccessor = StompHeaderAccessor.wrap(outbound);
        assertThat(outbound.getPayload()).isEqualTo(payload);
        assertThat(outboundAccessor.getDestination()).isEqualTo("/topic/chat/rooms/room-1/read-state");
        assertThat(outboundAccessor.getSubscriptionId()).isEqualTo("sub-read-state");
        assertThat(outboundAccessor.getSessionId()).isEqualTo("session-1");
    }
}
