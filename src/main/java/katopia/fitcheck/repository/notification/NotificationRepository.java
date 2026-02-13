package katopia.fitcheck.repository.notification;

import katopia.fitcheck.domain.notification.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            select n from Notification n
            where n.recipient.id = :recipientId
            order by n.createdAt desc, n.id desc
            """)
    List<Notification> findLatestByRecipientId(@Param("recipientId") Long recipientId, Pageable pageable);

    @Query("""
            select n from Notification n
            where n.recipient.id = :recipientId
              and ((n.createdAt < :createdAt)
               or (n.createdAt = :createdAt and n.id < :id))
            order by n.createdAt desc, n.id desc
            """)
    List<Notification> findPageAfter(
            @Param("recipientId") Long recipientId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    java.util.Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);
}
