package katopia.fitcheck.repository.notification;

import katopia.fitcheck.domain.notification.Notification;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Notification notification = notifications.get(i);
                if (notification.getActor() != null) {
                    ps.setLong(1, notification.getActor().getId());
                } else {
                    ps.setObject(1, null);
                }
                ps.setString(2, notification.getActorNicknameSnapshot());
                ps.setTimestamp(3, Timestamp.valueOf(notification.getCreatedAt()));
                ps.setString(4, notification.getImageObjectKeySnapshot());
                ps.setString(5, notification.getMessage());
                ps.setString(6, notification.getNotificationType().name());
                if (notification.getReadAt() != null) {
                    ps.setTimestamp(7, Timestamp.valueOf(notification.getReadAt()));
                } else {
                    ps.setObject(7, null);
                }
                ps.setLong(8, notification.getRecipient().getId());
                ps.setLong(9, notification.getRefId());
            }

            @Override
            public int getBatchSize() {
                return notifications.size();
            }
        });
    }
}
