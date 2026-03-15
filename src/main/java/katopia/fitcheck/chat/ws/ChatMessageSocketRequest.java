package katopia.fitcheck.chat.ws;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatMessageSocketRequest(
        @Schema(description = "채팅방 ID(ObjectId 문자열)", example = "67d2f7c4a8b13e4d91c0ab12")
        String roomId,

        @Schema(description = "텍스트 메시지 본문", example = "안녕")
        String message,

        @Schema(description = "이미지 메시지용 object key", example = "chat/rooms/67d2f7c4a8b13e4d91c0ab12/sample.webp", nullable = true)
        String imageObjectKey
) {
}
