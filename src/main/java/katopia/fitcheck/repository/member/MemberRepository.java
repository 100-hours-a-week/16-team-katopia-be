package katopia.fitcheck.repository.member;

import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauth2ProviderAndOauth2UserId(SocialProvider provider, String oauth2UserId);

    boolean existsByNickname(String nickname);

    boolean existsByIdAndAccountStatus(Long memberId, AccountStatus status);

    Optional<Member> findByIdAndAccountStatus(Long memberId, AccountStatus status);

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and m.nickname like concat(:nickname, '%') escape '\\'
            order by m.createdAt desc, m.id desc
            """)
    List<Member> searchLatestByNickname(
            @Param("nickname") String nickname,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and m.nickname like concat(:nickname, '%') escape '\\'
              and ((m.createdAt < :createdAt) or (m.createdAt = :createdAt and m.id < :id))
            order by m.createdAt desc, m.id desc
            """)
    List<Member> searchPageAfterByNickname(
            @Param("nickname") String nickname,
            @Param("status") AccountStatus status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and m.id in :ids
            order by m.createdAt desc, m.id desc
            """)
    List<Member> findActiveByIdsOrderByLatest(
            @Param("status") AccountStatus status,
            @Param("ids") List<Long> ids
    );

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and (:excludeEmpty = true or m.id not in :excludeIds)
            order by m.createdAt desc, m.id desc
            """)
    List<Member> findLatestActive(
            @Param("status") AccountStatus status,
            @Param("excludeEmpty") boolean excludeEmpty,
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable
    );


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Member m
            set m.postCount = m.postCount + 1
            where m.id = :memberId
            """)
    int incrementPostCount(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Member m
            set m.postCount = case when m.postCount > 0 then m.postCount - 1 else 0 end
            where m.id = :memberId
            """)
    int decrementPostCount(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Member m
            set m.followingCount = m.followingCount + 1
            where m.id = :memberId
            """)
    int incrementFollowingCount(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Member m
            set m.followingCount = case when m.followingCount > 0 then m.followingCount - 1 else 0 end
            where m.id = :memberId
            """)
    int decrementFollowingCount(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Member m
            set m.followerCount = m.followerCount + 1
            where m.id = :memberId
            """)
    int incrementFollowerCount(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Member m
            set m.followerCount = m.followerCount + :delta
            where m.id = :memberId
            """)
    int incrementFollowerCountBy(@Param("memberId") Long memberId, @Param("delta") long delta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Member m
            set m.followerCount = case when m.followerCount > 0 then m.followerCount - 1 else 0 end
            where m.id = :memberId
            """)
    int decrementFollowerCount(@Param("memberId") Long memberId);
}
