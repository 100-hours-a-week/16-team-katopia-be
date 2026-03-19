package katopia.fitcheck.chat.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatRoomAllListResponse(
        @Schema(description = "전체 채팅방 목록")
        List<ChatRoomAllSummaryResponse> rooms,

        @Schema(description = "다음 페이지 커서(updatedAt|roomId)", example = "2026-03-11T12:34:56Z|67d2f7c4a8b13e4d91c0ab12")
        String nextCursor
) {
    public static ChatRoomAllListResponse of(List<ChatRoomAllSummaryResponse> rooms, String nextCursor) {
        return ChatRoomAllListResponse.builder()
                .rooms(rooms)
                .nextCursor(nextCursor)
                .build();
    }
}
