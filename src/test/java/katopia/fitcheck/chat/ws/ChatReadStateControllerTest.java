package katopia.fitcheck.chat.ws;

import katopia.fitcheck.chat.service.message.ChatReadStateService;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.redis.chat.RedisChatRealtimePublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatReadStateControllerTest {

    @Mock
    private ChatReadStateService chatReadStateService;

    @Mock
    private SecuritySupport securitySupport;

    @Mock
    private RedisChatRealtimePublisher redisChatRealtimePublisher;

    @InjectMocks
    private ChatReadStateController chatReadStateController;

    @Test
    @DisplayName("TC-CHAT-WS-S-02 READ_STATE 이벤트 수신 시 읽음 토픽으로 브로드캐스트")
    void tcChatWsS02_readState_broadcastsToRoomTopic() {
        ChatReadStateRequest request = new ChatReadStateRequest("room-1", 101L);
        MemberPrincipal principal = new MemberPrincipal(7L);
        ChatReadStateResponse response = ChatReadStateResponse.of("room-1", 7L, 101L);

        when(securitySupport.requireMemberId((Principal) principal)).thenReturn(7L);
        when(chatReadStateService.acknowledge(7L, "room-1", 101L)).thenReturn(response);

        chatReadStateController.readState(request, principal);

        verify(redisChatRealtimePublisher).publishReadState(response);
    }
}
