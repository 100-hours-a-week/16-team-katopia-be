package katopia.fitcheck.chat.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatMessageListResponse(
        @Schema(description = "메시지 목록")
        List<ChatMessageResponse> messages,

        @Schema(description = "다음 페이지 커서(messageId 기준)", example = "101")
        String nextCursor
) {
    public static ChatMessageListResponse of(List<ChatMessageResponse> messages, String nextCursor) {
        return ChatMessageListResponse.builder()
                .messages(messages)
                .nextCursor(nextCursor)
                .build();
    }
}
