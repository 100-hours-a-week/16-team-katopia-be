package katopia.fitcheck.service.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {

    private String actorNicknameSnapshot;
    private String imageObjectKeySnapshot;
    private String[] messageArgs;
}
