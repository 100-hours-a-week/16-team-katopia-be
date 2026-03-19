package katopia.fitcheck.chat.ws;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatReadStateRequest(
        @Schema(description = "채팅방 ID(ObjectId 문자열)", example = "67d2f7c4a8b13e4d91c0ab12")
        String roomId,

        @Schema(description = "클라이언트가 마지막으로 읽은 메시지 ID", example = "12345")
        Long lastReadMessageId
) {
}
