package katopia.fitcheck.repository.member;

import katopia.fitcheck.domain.member.MemberFollow;
import katopia.fitcheck.dto.member.response.MemberFollowSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    Optional<MemberFollow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    @Query("""
            select new katopia.fitcheck.dto.member.response.MemberFollowSummary(
                    f.id,
                    f.createdAt,
                    m.id,
                    m.nickname,
                    m.profileImageObjectKey
            )
            from MemberFollow f
            join f.follower m
            where f.followed.id = :memberId
            order by f.createdAt desc, f.id desc
            """)
    List<MemberFollowSummary> findFollowersLatest(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query("""
            select new katopia.fitcheck.dto.member.response.MemberFollowSummary(
                    f.id,
                    f.createdAt,
                    m.id,
                    m.nickname,
                    m.profileImageObjectKey
            )
            from MemberFollow f
            join f.follower m
            where f.followed.id = :memberId
              and ((f.createdAt < :createdAt) or (f.createdAt = :createdAt and f.id < :id))
            order by f.createdAt desc, f.id desc
            """)
    List<MemberFollowSummary> findFollowersPageAfter(
            @Param("memberId") Long memberId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select new katopia.fitcheck.dto.member.response.MemberFollowSummary(
                    f.id,
                    f.createdAt,
                    m.id,
                    m.nickname,
                    m.profileImageObjectKey
            )
            from MemberFollow f
            join f.followed m
            where f.follower.id = :memberId
            order by f.createdAt desc, f.id desc
            """)
    List<MemberFollowSummary> findFollowingsLatest(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query("""
            select new katopia.fitcheck.dto.member.response.MemberFollowSummary(
                    f.id,
                    f.createdAt,
                    m.id,
                    m.nickname,
                    m.profileImageObjectKey
            )
            from MemberFollow f
            join f.followed m
            where f.follower.id = :memberId
              and ((f.createdAt < :createdAt) or (f.createdAt = :createdAt and f.id < :id))
            order by f.createdAt desc, f.id desc
            """)
    List<MemberFollowSummary> findFollowingsPageAfter(
            @Param("memberId") Long memberId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select f.followed.id from MemberFollow f
            where f.follower.id = :followerId
              and f.followed.id in :targetIds
            """)
    Set<Long> findFollowedIds(
            @Param("followerId") Long followerId,
            @Param("targetIds") List<Long> targetIds
    );

    @Query("""
            select f.followed.id from MemberFollow f
            where f.follower.id = :followerId
            """)
    List<Long> findFollowedIdsByFollowerId(@Param("followerId") Long followerId);

    @Query("""
            select f.followed.id from MemberFollow f
            where f.follower.id in :followerIds
            """)
    List<Long> findFollowedIdsByFollowerIds(@Param("followerIds") List<Long> followerIds);
}
