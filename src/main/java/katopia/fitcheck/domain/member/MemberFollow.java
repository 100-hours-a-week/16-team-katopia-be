package katopia.fitcheck.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "member_follows",
        uniqueConstraints = {
                @UniqueConstraint(name = "uidx_member_follows_follower_followed", columnNames = {"follower_id", "followed_id"})
        },
        indexes = {
                @Index(name = "idx_member_follows_follower_created", columnList = "follower_id, created_at"),
                @Index(name = "idx_member_follows_followed_created", columnList = "followed_id, created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "followed_id", nullable = false)
    private Member followed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private MemberFollow(Member follower, Member followed) {
        this.follower = follower;
        this.followed = followed;
    }

    public static MemberFollow of(Member follower, Member followed) {
        return new MemberFollow(follower, followed);
    }
}
