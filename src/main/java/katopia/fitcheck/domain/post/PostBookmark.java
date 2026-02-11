package katopia.fitcheck.domain.post;

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
import katopia.fitcheck.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "post_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(name = "uidx_post_bookmarks_member_post", columnNames = {"member_id", "post_id"})
        },
        indexes = {
                @Index(name = "idx_post_bookmarks_member", columnList = "member_id, created_at"),
                @Index(name = "idx_post_bookmarks_post", columnList = "post_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PostBookmark(Member member, Post post) {
        this.member = member;
        this.post = post;
    }

    public static PostBookmark of(Member member, Post post) {
        return new PostBookmark(member, post);
    }
}
