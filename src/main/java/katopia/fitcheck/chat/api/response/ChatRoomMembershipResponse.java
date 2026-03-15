package katopia.fitcheck.chat.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.chat.domain.ChatRoomDocument;

public record ChatRoomMembershipResponse(
        @Schema(description = "채팅방 ID(ObjectId 문자열)", example = "67d2f7c4a8b13e4d91c0ab12")
        String roomId,

        @Schema(description = "채팅방 제목", example = "핏체크 같이 할 사람들")
        String title,

        @Schema(description = "참여 인원 수", example = "2")
        int participantCount,

        @Schema(description = "현재 사용자의 참여 여부", example = "true")
        boolean joined
) {
    public static ChatRoomMembershipResponse joined(ChatRoomDocument room) {
        return new ChatRoomMembershipResponse(room.getId(), room.getTitle(), room.getParticipantCount(), true);
    }

    public static ChatRoomMembershipResponse left(ChatRoomDocument room) {
        return new ChatRoomMembershipResponse(room.getId(), room.getTitle(), room.getParticipantCount(), false);
    }
}
