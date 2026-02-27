package katopia.fitcheck.dto.notification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record NotificationListResponse(
        @Schema(description = Docs.NOTIFICATION_LIST_DES)
        List<NotificationSummary> notifications,
        @Schema(description = Docs.CURSOR_DES, example = Docs.CURSOR)
        String nextCursor
) {
    public static NotificationListResponse of(List<NotificationSummary> notifications, String nextCursor) {
        return NotificationListResponse.builder()
                .notifications(notifications)
                .nextCursor(nextCursor)
                .build();
    }
}
