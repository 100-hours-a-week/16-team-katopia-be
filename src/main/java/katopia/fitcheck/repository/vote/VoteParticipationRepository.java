package katopia.fitcheck.repository.vote;

import java.util.List;

import katopia.fitcheck.domain.vote.VoteParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteParticipationRepository extends JpaRepository<VoteParticipation, Long> {

    boolean existsByVoteIdAndMemberId(Long voteId, Long memberId);

    @Modifying
    @Query("delete from VoteParticipation vp where vp.vote.id = :voteId")
    void deleteByVoteId(@Param("voteId") Long voteId);

    @Query("select vp.member.id from VoteParticipation vp where vp.vote.id = :voteId")
    List<Long> findParticipantMemberIdsByVoteId(@Param("voteId") Long voteId);
}
