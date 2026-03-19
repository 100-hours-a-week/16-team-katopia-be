package katopia.fitcheck.chat.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatRoomJoinedListResponse(
        @Schema(description = "참여 중인 채팅방 목록")
        List<ChatRoomJoinedSummaryResponse> rooms,

        @Schema(description = "다음 페이지 커서", example = "2026-03-11T12:34:56Z|8f62c2a8-07d0-4711-8c23-ff2541c7194a")
        String nextCursor
) {
    public static ChatRoomJoinedListResponse of(List<ChatRoomJoinedSummaryResponse> rooms, String nextCursor) {
        return ChatRoomJoinedListResponse.builder()
                .rooms(rooms)
                .nextCursor(nextCursor)
                .build();
    }
}
