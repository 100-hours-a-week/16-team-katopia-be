package katopia.fitcheck.repository.notification;

import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.global.policy.Policy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NotificationBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificationBulkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;
        }
        String sql = """
                insert into notifications (
                    actor_id,
                    actor_nickname_snapshot,
                    created_at,
                    image_object_key_snapshot,
                    message,
                    notification_type,
                    read_at,
                    recipient_id,
                    ref_id
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        int batchSize = Policy.NOTIFICATION_BULK_BATCH_SIZE;
        int total = notifications.size();
        for (int start = 0; start < total; start += batchSize) {
            int end = Math.min(start + batchSize, total);
            List<Object[]> batchArgs = new ArrayList<>(end - start);
            LocalDateTime createdAt = notifications.get(start).getCreatedAt();
            Timestamp createdAtTs = createdAt != null ? Timestamp.valueOf(createdAt) : null;
            for (int i = start; i < end; i++) {
                Notification notification = notifications.get(i);
                Long actorId = notification.getActor() != null ? notification.getActor().getId() : null;
                Timestamp readAtTs = notification.getReadAt() != null ? Timestamp.valueOf(notification.getReadAt()) : null;
                batchArgs.add(new Object[]{
                        actorId,
                        notification.getActorNicknameSnapshot(),
                        createdAtTs,
                        notification.getImageObjectKeySnapshot(),
                        notification.getMessage(),
                        notification.getNotificationType().name(),
                        readAtTs,
                        notification.getRecipient().getId(),
                        notification.getRefId()
                });
            }
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }
}
