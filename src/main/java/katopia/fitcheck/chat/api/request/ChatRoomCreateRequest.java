package katopia.fitcheck.chat.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import katopia.fitcheck.global.policy.Policy;

public record ChatRoomCreateRequest(
        @Schema(
                description = "채팅방 제목",
                example = "핏체크 같이 할 사람들",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = Policy.CHAT_ROOM_TITLE_MAX_LENGTH
        )
        @NotBlank(message = "COMMON-E-001:채팅방 제목")
        @Size(
                min = 1,
                max = Policy.CHAT_ROOM_TITLE_MAX_LENGTH,
                message = "CHAT-E-001"
        )
        String title,

        @Schema(
                description = "채팅방 썸네일 이미지 오브젝트 키",
                example = "chat/rooms/550e8400-e29b-41d4-a716-446655440000.png",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = Policy.IMAGE_OBJECT_KEY_MAX_LENGTH
        )
        @Size(
                max = Policy.IMAGE_OBJECT_KEY_MAX_LENGTH,
                message = "CHAT-E-002"
        )
        String thumbnailImageObjectKey
) {
}
