package katopia.fitcheck.redis.sse;

import java.util.Optional;

public final class SseDisconnectMessageConverter {

    private static final String DELIMITER = "|";

    private SseDisconnectMessageConverter() { }

    public static String encode(Long memberId, String connectionId) {
        return memberId + DELIMITER + connectionId;
    }

    public static Optional<Parsed> decode(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        String[] parts = message.split("\\|", 2);
        if (parts.length != 2) {
            return Optional.empty();
        }
        try {
            Long memberId = Long.parseLong(parts[0]);
            String connectionId = parts[1];
            if (connectionId.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(new Parsed(memberId, connectionId));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public record Parsed(Long memberId, String connectionId) { }
}
