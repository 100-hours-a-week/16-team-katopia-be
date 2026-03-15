package katopia.fitcheck.chat.ws;

import java.time.Instant;

public record ChatReadStateResponse(
        String roomId,
        Long memberId,
        Long lastReadMessageId,
        Instant acknowledgedAt
) {
    public static ChatReadStateResponse of(String roomId, Long memberId, Long lastReadMessageId) {
        return new ChatReadStateResponse(roomId, memberId, lastReadMessageId, Instant.now());
    }
}
