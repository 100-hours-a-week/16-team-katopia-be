package katopia.fitcheck.dto.notification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationSummary(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.NOTIFICATION_TYPE_DES, example = Docs.NOTIFICATION_TYPE_EXAMPLE)
        NotificationType type,
        @Schema(description = Docs.NOTIFICATION_MESSAGE_DES, example = Docs.NOTIFICATION_MESSAGE)
        String message,
        @Schema(description = Docs.ID_DES, example = "1")
        Long referenceId,
        @Schema(description = Docs.AUTHOR_DES)
        NotificationActor actor,
        @Schema(description = Docs.CREATED_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime createdAt,
        @Schema(description = Docs.READ_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime readAt
) {
    public static NotificationSummary of(Notification notification) {
        return NotificationSummary.builder()
                .id(notification.getId())
                .type(notification.getNotificationType())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .actor(NotificationActor.of(notification))
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
