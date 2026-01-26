package katopia.fitcheck.post.repository;

import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            select p from Post p
            order by p.createdAt desc, p.id desc
            """)
    List<Post> findLatest(Pageable pageable);

    @Query("""
            select p from Post p
            where p.member.id = :memberId
            order by p.createdAt desc, p.id desc
            """)
    List<Post> findLatestByMemberId(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query("""
            select p from Post p
            where (p.createdAt < :createdAt)
               or (p.createdAt = :createdAt and p.id < :id)
            order by p.createdAt desc, p.id desc
            """)
    List<Post> findPageAfter(
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select p from Post p
            where p.member.id = :memberId
              and ((p.createdAt < :createdAt)
                or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<Post> findPageAfterByMemberId(
            @Param("memberId") Long memberId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select distinct p from Post p
            join fetch p.member m
            left join fetch p.images i
            where p.id = :id
            """)
    Optional<Post> findDetailById(@Param("id") Long id);

    @Query("""
            update Post p
            set p.likeCount = p.likeCount + 1
            where p.id = :id
            """)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    int incrementLikeCount(@Param("id") Long id);

    @Query("""
            update Post p
            set p.likeCount = p.likeCount - 1
            where p.id = :id and p.likeCount > 0
            """)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    int decrementLikeCount(@Param("id") Long id);

    @Query("""
            update Post p
            set p.commentCount = p.commentCount + 1
            where p.id = :id
            """)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    int incrementCommentCount(@Param("id") Long id);

    @Query("""
            update Post p
            set p.commentCount = p.commentCount - 1
            where p.id = :id and p.commentCount > 0
            """)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    int decrementCommentCount(@Param("id") Long id);

    @Query("""
            select p.likeCount from Post p where p.id = :id
            """)
    Long findLikeCountById(@Param("id") Long id);

    @Query("""
            select p.id from Post p where p.member.id = :memberId
            """)
    List<Long> findIdsByMemberId(@Param("memberId") Long memberId);

    @Query("""
            select p from Post p
            join p.member m
            where m.accountStatus = :status
              and p.content like concat(:query, '%')
            order by p.createdAt desc, p.id desc
            """)
    List<Post> searchLatestByContent(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
            select p from Post p
            join p.member m
            where m.accountStatus = :status
              and p.content like concat(:query, '%')
              and ((p.createdAt < :createdAt) or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<Post> searchPageAfterByContent(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select distinct p from Post p
            join p.member m
            join p.postTags pt
            join pt.tag t
            where m.accountStatus = :status
              and t.name like concat(:query, '%')
            order by p.createdAt desc, p.id desc
            """)
    List<Post> searchLatestByTag(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
            select distinct p from Post p
            join p.member m
            join p.postTags pt
            join pt.tag t
            where m.accountStatus = :status
              and t.name like concat(:query, '%')
              and ((p.createdAt < :createdAt) or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<Post> searchPageAfterByTag(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );
}
