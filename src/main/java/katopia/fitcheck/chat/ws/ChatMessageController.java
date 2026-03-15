package katopia.fitcheck.chat.ws;

import katopia.fitcheck.chat.api.request.ChatMessageCreateRequest;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.service.message.ChatMessageCommandService;
import katopia.fitcheck.global.security.SecuritySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageCommandService chatMessageCommandService;
    private final SecuritySupport securitySupport;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(ChatPolicy.MESSAGE_MAPPING)
    public void sendMessage(@Payload ChatMessageSocketRequest request, Principal principal) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatMessageResponse response = chatMessageCommandService.createMessage(
                memberId,
                request.roomId(),
                new ChatMessageCreateRequest(request.message(), request.imageObjectKey())
        );
        simpMessagingTemplate.convertAndSend(
                ChatPolicy.roomMessagesTopic(response.roomId()),
                response
        );
    }
}
