package katopia.fitcheck.domain.aggregation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "count_batches")
public class CountBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "processed_from", nullable = false)
    private LocalDateTime processedFrom;

    @Column(name = "processed_to", nullable = false)
    private LocalDateTime processedTo;

    @Column(name = "applied_post_count", nullable = false)
    private int appliedPostCount;

    @Column(name = "applied_delta", nullable = false)
    private int appliedDelta;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private CountBatch(
            LocalDateTime processedFrom,
            LocalDateTime processedTo,
            int appliedPostCount,
            int appliedDelta
    ) {
        this.processedFrom = processedFrom;
        this.processedTo = processedTo;
        this.appliedPostCount = appliedPostCount;
        this.appliedDelta = appliedDelta;
    }

    public static CountBatch of(
            LocalDateTime processedFrom,
            LocalDateTime processedTo,
            int appliedPostCount,
            int appliedDelta
    ) {
        return new CountBatch(processedFrom, processedTo, appliedPostCount, appliedDelta);
    }

    public void update(
            LocalDateTime processedFrom,
            LocalDateTime processedTo,
            int appliedPostCount,
            int appliedDelta
    ) {
        this.processedFrom = processedFrom;
        this.processedTo = processedTo;
        this.appliedPostCount = appliedPostCount;
        this.appliedDelta = appliedDelta;
    }
}
