package katopia.fitcheck.redis.chat;

import com.fasterxml.jackson.databind.JsonNode;

public record ChatRealtimeEnvelope(
        String destination,
        ChatRealtimePayloadType payloadType,
        JsonNode payload
) {
}
