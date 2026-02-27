package katopia.fitcheck.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {

    private String eventId;
    private String eventType;
    private String occurredAt;
    private Long actorId;
    private List<Long> targetIds;
    private Long refId;
    private String payloadType;
    private Object payload;

    public static String newEventId() {
        return UUID.randomUUID().toString();
    }

    public static String now() {
        return Instant.now().toString();
    }
}
