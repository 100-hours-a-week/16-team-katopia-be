package katopia.fitcheck.repository.vote;

import katopia.fitcheck.domain.vote.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {

    @Query("""
            select vi from VoteItem vi
            where vi.vote.id = :voteId
            order by vi.sortOrder asc
            """)
    List<VoteItem> findByVoteIdOrderBySortOrder(@Param("voteId") Long voteId);

    @Query("""
            select vi from VoteItem vi
            where vi.vote.id = :voteId
              and vi.id in :ids
            """)
    List<VoteItem> findByVoteIdAndIdIn(@Param("voteId") Long voteId, @Param("ids") List<Long> ids);

    @Modifying(clearAutomatically = true)
    @Query("""
            update VoteItem vi
            set vi.fitCount = vi.fitCount + 1
            where vi.vote.id = :voteId
              and vi.id in :ids
            """)
    int incrementFitCounts(@Param("voteId") Long voteId, @Param("ids") List<Long> ids);

    @Modifying
    @Query("delete from VoteItem vi where vi.vote.id = :voteId")
    void deleteByVoteId(@Param("voteId") Long voteId);
}
