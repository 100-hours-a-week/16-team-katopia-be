package katopia.fitcheck.chat.ws;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

public record ChatReadStateSnapshotResponse(
        @Schema(description = "채팅방 ID(ObjectId 문자열)", example = "69b693a7d009ab868d55284b")
        String roomId,

        @Schema(description = "채팅방 참여자 전체의 읽음 상태 목록")
        List<ChatParticipantReadState> participants,

        @Schema(description = "스냅샷 생성 시각", example = "2026-03-15T11:14:52.160205Z")
        Instant snapshotAt
) {

    public static ChatReadStateSnapshotResponse of(String roomId, List<ChatParticipantReadState> participants) {
        return new ChatReadStateSnapshotResponse(roomId, participants, Instant.now());
    }
}
