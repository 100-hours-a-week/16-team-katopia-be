package katopia.fitcheck.chat.ws;

import katopia.fitcheck.chat.service.message.ChatReadStateService;
import katopia.fitcheck.global.security.SecuritySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatReadStateController {

    private final ChatReadStateService chatReadStateService;
    private final SecuritySupport securitySupport;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(ChatPolicy.READ_STATE_MAPPING)
    public void readState(@Payload ChatReadStateRequest request, Principal principal) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatReadStateResponse response = chatReadStateService.acknowledge(
                memberId,
                request.roomId(),
                request.lastReadMessageId()
        );
        simpMessagingTemplate.convertAndSend(
                ChatPolicy.roomReadStateTopic(response.roomId()),
                response
        );
    }
}
