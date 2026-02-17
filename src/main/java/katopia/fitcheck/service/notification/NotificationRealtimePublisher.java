package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.notification.Notification;

public interface NotificationRealtimePublisher {

    void publish(Notification notification);
}
