package katopia.fitcheck.dto.notification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.global.docs.Docs;

public record NotificationActor(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.NICKNAME_DES, example = Docs.NICKNAME)
        String nicknameSnapshot,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String profileImageObjectKeySnapshot
) {
    public static NotificationActor of(Notification notification) {
        Long actorId = notification.getActor() != null ? notification.getActor().getId() : null;
        return new NotificationActor(
                actorId,
                notification.getActorNicknameSnapshot(),
                notification.getActorProfileImageObjectKeySnapshot()
        );
    }
}
