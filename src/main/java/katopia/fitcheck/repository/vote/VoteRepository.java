package katopia.fitcheck.repository.vote;

import katopia.fitcheck.domain.vote.Vote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("""
            select v from Vote v
            where v.member.id = :memberId
            order by v.createdAt desc, v.id desc
            """)
    List<Vote> findLatestByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("""
            select v from Vote v
            where v.member.id = :memberId
              and ((v.createdAt < :createdAt)
               or (v.createdAt = :createdAt and v.id < :id))
            order by v.createdAt desc, v.id desc
            """)
    List<Vote> findPageAfter(
            @Param("memberId") Long memberId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select v from Vote v
            where v.member.id <> :memberId
              and v.expiresAt > :now
              and not exists (
                    select 1 from VoteParticipation vp
                    where vp.vote = v and vp.member.id = :memberId
              )
            order by v.createdAt desc, v.id desc
            """)
    List<Vote> findLatestCandidate(
            @Param("memberId") Long memberId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

}
