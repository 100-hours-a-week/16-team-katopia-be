package katopia.fitcheck.chat.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.chat.domain.ChatRoomDocument;

import java.time.Instant;

public record ChatRoomAllSummaryResponse(
        @Schema(description = "채팅방 ID(ObjectId 문자열)", example = "67d2f7c4a8b13e4d91c0ab12")
        String roomId,

        @Schema(description = "채팅방 생성자 ID", example = "1")
        Long ownerId,

        @Schema(description = "채팅방 제목", example = "핏체크 같이 할 사람들")
        String title,

        @Schema(description = "채팅방 썸네일 이미지 오브젝트 키", example = "chat/rooms/67d2f7c4a8b13e4d91c0ab12/thumbnail.webp")
        String thumbnailImageObjectKey,

        @Schema(description = "참여 인원 수", example = "4")
        int participantCount,

        @Schema(description = "현재 로그인 사용자의 채팅방 참여 여부", example = "true")
        boolean joined,

        @Schema(description = "생성 시각(UTC)", example = "2026-03-11T12:34:56Z")
        Instant createdAt,

        @Schema(description = "수정 시각(UTC)", example = "2026-03-11T12:34:56Z")
        Instant updatedAt
) {
    public static ChatRoomAllSummaryResponse from(ChatRoomDocument room, boolean joined) {
        return new ChatRoomAllSummaryResponse(
                room.getId(),
                room.getOwnerId(),
                room.getTitle(),
                room.getThumbnailImageObjectKey(),
                room.getParticipantCount(),
                joined,
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
