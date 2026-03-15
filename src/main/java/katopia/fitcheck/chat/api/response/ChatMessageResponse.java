package katopia.fitcheck.chat.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.chat.domain.ChatMessageDocument;
import katopia.fitcheck.chat.domain.ChatMessageType;

import java.time.Instant;

public record ChatMessageResponse(
        @Schema(description = "메시지 문서 ID", example = "8f62c2a8-07d0-4711-8c23-ff2541c7194a")
        String id,

        @Schema(description = "메시지 번호(lastReadMessageId 기준)", example = "101")
        Long messageId,

        @Schema(description = "채팅방 ID(ObjectId 문자열)", example = "67d2f7c4a8b13e4d91c0ab12")
        String roomId,

        @Schema(description = "발신자 회원 ID", example = "7")
        Long senderId,

        @Schema(description = "발신자 닉네임 스냅샷", example = "핏체커")
        String senderNicknameSnapshot,

        @Schema(description = "발신자 프로필 이미지 오브젝트 키 스냅샷", example = "profiles/7/profile.png")
        String senderProfileImageObjectKeySnapshot,

        @Schema(description = "메시지 본문", example = "오늘 코디 어때?")
        String message,

        @Schema(description = "이미지 오브젝트 키", example = "chat/messages/550e8400-e29b-41d4-a716-446655440000.png")
        String imageObjectKey,

        @Schema(description = "메시지 타입", example = "TEXT")
        ChatMessageType messageType,

        @Schema(description = "생성 시각(UTC)", example = "2026-03-13T12:34:56Z")
        Instant createdAt
) {
    public static ChatMessageResponse from(ChatMessageDocument message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getMessageId(),
                message.getRoomId(),
                message.getSenderId(),
                message.getSenderNicknameSnapshot(),
                message.getSenderProfileImageObjectKeySnapshot(),
                message.getMessage(),
                message.getImageObjectKey(),
                message.getMessageType(),
                message.getCreatedAt()
        );
    }
}
