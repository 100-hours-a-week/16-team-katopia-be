package katopia.fitcheck.chat.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.chat.domain.ChatRoomDocument;

import java.time.Instant;

public record ChatRoomCreateResponse(
        @Schema(description = "채팅방 ID(ObjectId 문자열)", example = "67d2f7c4a8b13e4d91c0ab12")
        String roomId,

        @Schema(description = "채팅방 생성자 ID", example = "1")
        Long ownerId,

        @Schema(description = "채팅방 제목", example = "핏체크 같이 할 사람들")
        String title,

        @Schema(description = "참여 인원 수", example = "1")
        int participantCount,

        @Schema(description = "채팅방 썸네일 이미지 오브젝트 키", example = "chat/rooms/550e8400-e29b-41d4-a716-446655440000.png")
        String thumbnailImageObjectKey,

        @Schema(description = "생성 시각(UTC)", example = "2026-03-11T12:34:56Z")
        Instant createdAt,

        @Schema(description = "수정 시각(UTC)", example = "2026-03-11T12:34:56Z")
        Instant updatedAt
) {
    public static ChatRoomCreateResponse from(ChatRoomDocument room) {
        return new ChatRoomCreateResponse(
                room.getId(),
                room.getOwnerId(),
                room.getTitle(),
                room.getParticipantCount(),
                room.getThumbnailImageObjectKey(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
