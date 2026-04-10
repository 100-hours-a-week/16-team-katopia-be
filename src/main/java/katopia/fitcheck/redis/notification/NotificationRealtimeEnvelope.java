package katopia.fitcheck.redis.notification;

import katopia.fitcheck.dto.notification.response.NotificationSummary;

public record NotificationRealtimeEnvelope(
        Long recipientId,
        NotificationSummary payload
) {
}
