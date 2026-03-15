package katopia.fitcheck.chat.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.chat.ws.ChatMessageSocketRequest;
import katopia.fitcheck.chat.ws.ChatPolicy;
import katopia.fitcheck.chat.ws.ChatReadStateRequest;

import java.io.IOException;

@Tag(name = "Chat", description = "채팅방/채팅 메시지 REST API")
public interface ChatSocketDocsApiSpec {

    String DOCS_ONLY_DESCRIPTION = "Swagger 문서용 비활성 엔드포인트입니다. ";
    String CONNECT_FRAME_EXAMPLE = """
            실제 STOMP CONNECT 프레임 예시:
            ```text
            CONNECT
            accept-version:1.2
            host:{{host}}
            Authorization:Bearer {{AT}}

            \\0
            ```
            """;
    String SUBSCRIBE_MESSAGES_FRAME_EXAMPLE = """
            실제 STOMP 프레임 예시:
            ```text
            SUBSCRIBE
            id:sub-messages
            destination:""" + ChatPolicy.ROOM_MESSAGES_TOPIC_TEMPLATE + """

            \\0
            ```
            """;
    String SUBSCRIBE_READ_STATE_FRAME_EXAMPLE = """
            실제 STOMP 프레임 예시:
            ```text
            SUBSCRIBE
            id:sub-read-state
            destination:""" + ChatPolicy.ROOM_READ_STATE_TOPIC_TEMPLATE + """

            \\0
            ```
            """;
    String SEND_MESSAGE_FRAME_EXAMPLE = """
            실제 STOMP 프레임 예시:
            ```text
            SEND
            destination:""" + ChatPolicy.MESSAGE_SEND_DESTINATION + """
            content-type:application/json

            {"roomId":"67d2f7c4a8b13e4d91c0ab12","message":"안녕","imageObjectKey":null}\\0
            ```
            """;
    String SEND_READ_STATE_FRAME_EXAMPLE = """
            실제 STOMP 프레임 예시:
            ```text
            SEND
            destination:""" + ChatPolicy.READ_STATE_SEND_DESTINATION + """
            content-type:application/json

            {"roomId":"67d2f7c4a8b13e4d91c0ab12","lastReadMessageId":12345}\\0
            ```
            """;
    String CONNECT_DESCRIPTION = DOCS_ONLY_DESCRIPTION
            + "실제 WebSocket endpoint는 `" + ChatPolicy.ENDPOINT + "` 이고, "
            + "연결 후 STOMP CONNECT 프레임에 `Authorization: Bearer {AT}` 헤더를 담아 보내야 합니다.\n\n"
            + CONNECT_FRAME_EXAMPLE;
    String SUBSCRIBE_MESSAGES_DESCRIPTION = DOCS_ONLY_DESCRIPTION
            + "메시지 topic 구독용 문서입니다.\n\n"
            + SUBSCRIBE_MESSAGES_FRAME_EXAMPLE;
    String SUBSCRIBE_READ_STATE_DESCRIPTION = DOCS_ONLY_DESCRIPTION
            + "읽음 상태 topic 구독용 문서입니다.\n\n"
            + SUBSCRIBE_READ_STATE_FRAME_EXAMPLE;
    String SEND_MESSAGE_DESCRIPTION = DOCS_ONLY_DESCRIPTION
            + "실제 destination은 `" + ChatPolicy.MESSAGE_SEND_DESTINATION + "` 입니다. "
            + "WebSocket 연결과 STOMP CONNECT가 먼저 성공해야 합니다.\n\n"
            + SEND_MESSAGE_FRAME_EXAMPLE;
    String SEND_READ_STATE_DESCRIPTION = DOCS_ONLY_DESCRIPTION
            + "실제 destination은 `" + ChatPolicy.READ_STATE_SEND_DESTINATION + "` 입니다. "
            + "WebSocket 연결과 STOMP CONNECT가 먼저 성공해야 합니다.\n\n"
            + SEND_READ_STATE_FRAME_EXAMPLE;

    @Operation(
            summary = "[문서용] STOMP CONNECT",
            description = CONNECT_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(responseCode = "501", description = "Swagger 문서용(실행 불가)", content = @Content)
    })
    void connectDocs(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "[문서용] STOMP SUBSCRIBE - 메시지 구독",
            description = SUBSCRIBE_MESSAGES_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(responseCode = "501", description = "Swagger 문서용(실행 불가)", content = @Content)
    })
    void subscribeMessagesDocs(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "[문서용] STOMP SUBSCRIBE - 읽음 상태 구독",
            description = SUBSCRIBE_READ_STATE_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(responseCode = "501", description = "Swagger 문서용(실행 불가)", content = @Content)
    })
    void subscribeReadStateDocs(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "[문서용] STOMP SEND - 채팅 메시지 전송",
            description = SEND_MESSAGE_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "501",
                    description = "Swagger 문서용(실행 불가)",
                    content = @Content(schema = @Schema(implementation = ChatMessageSocketRequest.class))
            )
    })
    void sendMessageDocs(ChatMessageSocketRequest request, HttpServletResponse response) throws IOException;

    @Operation(
            summary = "[문서용] STOMP SEND - 읽음 ACK 전송",
            description = SEND_READ_STATE_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "501",
                    description = "Swagger 문서용(실행 불가)",
                    content = @Content(schema = @Schema(implementation = ChatReadStateRequest.class))
            )
    })
    void sendReadStateDocs(ChatReadStateRequest request, HttpServletResponse response) throws IOException;
}
