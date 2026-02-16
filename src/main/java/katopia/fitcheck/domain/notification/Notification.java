package katopia.fitcheck.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.policy.Policy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_recipient_created", columnList = "recipient_id, created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Member recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Member actor;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "notification_type",
            nullable = false,
            columnDefinition = "enum('FOLLOW','POST_LIKE','POST_COMMENT','VOTE_CLOSED')"
    )
    private NotificationType notificationType;

    @Column(name = "actor_nickname_snapshot", length = 20, nullable = false)
    private String actorNicknameSnapshot;

    @Column(name = "image_object_key_snapshot", length = Policy.IMAGE_OBJECT_KEY_MAX_LENGTH)
    private String imageObjectKeySnapshot;

    @Column(name = "message", length = 255, nullable = false)
    private String message;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Builder
    private Notification(Member recipient,
                         Member actor,
                         NotificationType notificationType,
                         String actorNicknameSnapshot,
                         String imageObjectKeySnapshot,
                         String message,
                         Long refId
    ) {
        this.recipient = recipient;
        this.actor = actor;
        this.notificationType = notificationType;
        this.actorNicknameSnapshot = actorNicknameSnapshot;
        this.imageObjectKeySnapshot = imageObjectKeySnapshot;
        this.message = message;
        this.refId = refId;
    }

    public static Notification of(Member recipient,
                                  Member actor,
                                  NotificationType type,
                                  String message,
                                  Long refId,
                                  String imageObjectKeySnapshot
    ) {
        return Notification.builder()
                .recipient(recipient)
                .actor(actor)
                .notificationType(type)
                .actorNicknameSnapshot(actor != null ? actor.getNickname() : Policy.SYSTEM)
                .imageObjectKeySnapshot(imageObjectKeySnapshot)
                .message(message)
                .refId(refId)
                .build();
    }

    public void markRead(LocalDateTime readAt) {
        if (this.readAt == null) {
            this.readAt = readAt;
        }
    }
}
