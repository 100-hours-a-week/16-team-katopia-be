package katopia.fitcheck.chat.controller;

import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.chat.controller.spec.ChatSocketDocsApiSpec;
import katopia.fitcheck.chat.ws.ChatMessageSocketRequest;
import katopia.fitcheck.chat.ws.ChatReadStateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat/socket-docs")
public class ChatSocketDocsController implements ChatSocketDocsApiSpec {

    private static final String DOCS_ONLY_MESSAGE = "Swagger 문서용 비활성 엔드포인트입니다. 실제 채팅은 WebSocket/STOMP로 호출해주세요.";

    @Override
    @PostMapping("/connect")
    public void connectDocs(HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }

    @Override
    @PostMapping("/subscribe/messages")
    public void subscribeMessagesDocs(HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }

    @Override
    @PostMapping("/subscribe/read-state")
    public void subscribeReadStateDocs(HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }

    @Override
    @PostMapping("/unsubscribe/messages")
    public void unsubscribeMessagesDocs(HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }

    @Override
    @PostMapping("/unsubscribe/read-state")
    public void unsubscribeReadStateDocs(HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }

    @Override
    @PostMapping("/disconnect")
    public void disconnectDocs(HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }

    @Override
    @PostMapping("/send/message")
    public void sendMessageDocs(@RequestBody ChatMessageSocketRequest request,
                                HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }

    @Override
    @PostMapping("/send/read-state")
    public void sendReadStateDocs(@RequestBody ChatReadStateRequest request,
                                  HttpServletResponse response) throws IOException {
        response.sendError(501, DOCS_ONLY_MESSAGE);
    }
}
