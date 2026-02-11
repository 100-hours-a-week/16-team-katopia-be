package katopia.fitcheck.domain.vote;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.dto.vote.request.VoteCreateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(
        name = "votes",
        indexes = {
                @Index(name = "idx_votes_author_created", columnList = "member_id, created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    private static final int DEFAULT_EXPIRES_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 20, nullable = false)
    private String title;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;


    @OneToMany(mappedBy = "vote", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("sortOrder ASC")
    private List<VoteItem> items = new ArrayList<>();

    @Builder
    private Vote(Member member, String title, LocalDateTime expiresAt, List<VoteItem> items) {
        this.member = member;
        this.title = title;
        this.expiresAt = expiresAt;
        if (items != null) {
            this.items.addAll(items);
        }
    }

    static Vote create(Member member, String title, LocalDateTime expiresAt, List<VoteItem> items) {
        return Vote.builder()
                .member(member)
                .title(title)
                .expiresAt(expiresAt)
                .items(items)
                .build();
    }

    public static Vote create(Member member, VoteCreateRequest request) {
        String normalizedTitle = request.title().trim();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(DEFAULT_EXPIRES_HOURS);
        Vote vote = create(member, normalizedTitle, expiresAt, List.of());
        vote.addItemsFromKeys(request.imageObjectKeys());
        return vote;
    }

    private void addItemsFromKeys(List<String> imageObjectKeys) {
        if (imageObjectKeys == null || imageObjectKeys.isEmpty()) {
            return;
        }
        int order = 1;
        for (String key : imageObjectKeys) {
            items.add(VoteItem.of(this, order, key));
            order += 1;
        }
    }
}
