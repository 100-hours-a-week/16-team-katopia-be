package katopia.fitcheck.domain.vote;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "vote_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vote_items_vote_order", columnNames = {"vote_id", "sort_order"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column(name = "image_object_key", length = 1024, nullable = false)
    private String imageObjectKey;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "fit_count", nullable = false, columnDefinition = "bigint default 0")
    private long fitCount;

    @Builder
    private VoteItem(Vote vote, String imageObjectKey, int sortOrder, long fitCount) {
        this.vote = vote;
        this.imageObjectKey = imageObjectKey;
        this.sortOrder = sortOrder;
        this.fitCount = fitCount;
    }

    public static VoteItem of(Vote vote, int sortOrder, String imageObjectKey) {
        return VoteItem.builder()
                .vote(vote)
                .imageObjectKey(imageObjectKey)
                .sortOrder(sortOrder)
                .fitCount(0L)
                .build();
    }
}
