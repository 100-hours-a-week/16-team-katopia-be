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
import katopia.fitcheck.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "vote_participations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uidx_vote_participations_vote_member", columnNames = {"vote_id", "member_id"})
        },
        indexes = {
                @Index(name = "idx_vote_participations_vote_member", columnList = "vote_id, member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Builder
    private VoteParticipation(Vote vote, Member member, LocalDateTime completedAt) {
        this.vote = vote;
        this.member = member;
        this.completedAt = completedAt;
    }

    public static VoteParticipation of(Vote vote, Member member, LocalDateTime completedAt) {
        return VoteParticipation.builder()
                .vote(vote)
                .member(member)
                .completedAt(completedAt)
                .build();
    }
}
