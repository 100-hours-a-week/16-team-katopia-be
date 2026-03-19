package katopia.fitcheck.chat.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import katopia.fitcheck.global.policy.Policy;

public record ChatMessageCreateRequest(
        @Schema(
                description = "텍스트 메시지 본문(이미지 메시지일 경우 비움)",
                example = "오늘 코디 어때?",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                minLength = 1,
                maxLength = Policy.CHAT_MESSAGE_MAX_LENGTH
        )
        @Size(min = 1, max = Policy.CHAT_MESSAGE_MAX_LENGTH, message = "CHAT-E-009")
        String message,

        @Schema(
                description = "이미지 메시지 오브젝트 키(텍스트 메시지일 경우 비움)",
                example = "chat/messages/550e8400-e29b-41d4-a716-446655440000.png",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = Policy.IMAGE_OBJECT_KEY_MAX_LENGTH
        )
        @Size(max = Policy.IMAGE_OBJECT_KEY_MAX_LENGTH, message = "CHAT-E-010")
        String imageObjectKey
) {
}
