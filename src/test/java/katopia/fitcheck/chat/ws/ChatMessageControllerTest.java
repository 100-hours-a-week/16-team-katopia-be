package katopia.fitcheck.chat.ws;

import katopia.fitcheck.chat.api.request.ChatMessageCreateRequest;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.service.message.ChatMessageCommandService;
import katopia.fitcheck.chat.domain.ChatMessageType;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.redis.chat.RedisChatRealtimePublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageControllerTest {

    @Mock
    private ChatMessageCommandService chatMessageCommandService;

    @Mock
    private SecuritySupport securitySupport;

    @Mock
    private RedisChatRealtimePublisher redisChatRealtimePublisher;

    @InjectMocks
    private ChatMessageController chatMessageController;

    @Test
    @DisplayName("TC-CHAT-WS-S-01 MESSAGE 이벤트 수신 시 저장 후 방 토픽으로 브로드캐스트")
    void tcChatWsS01_sendMessage_broadcastsToRoomTopic() {
        ChatMessageSocketRequest request = new ChatMessageSocketRequest("room-1", "안녕하세요", null);
        MemberPrincipal principal = new MemberPrincipal(7L);
        ChatMessageResponse response = new ChatMessageResponse(
                "doc-1",
                101L,
                "room-1",
                7L,
                "핏체커",
                "profiles/7/profile.png",
                "안녕하세요",
                null,
                ChatMessageType.TEXT,
                Instant.parse("2026-03-13T12:34:56Z")
        );

        when(securitySupport.requireMemberId((Principal) principal)).thenReturn(7L);
        when(chatMessageCommandService.createMessage(eq(7L), eq("room-1"), any(ChatMessageCreateRequest.class)))
                .thenReturn(response);

        chatMessageController.sendMessage(request, principal);

        verify(redisChatRealtimePublisher).publishMessage(response);
    }
}
