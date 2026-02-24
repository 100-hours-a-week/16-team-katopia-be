package katopia.fitcheck.repository.post;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int incrementLikeCount(@Param("id") Long id);

    @Query("""
            update Post p
            set p.likeCount = p.likeCount - 1
            where p.id = :id and p.likeCount > 0
            """)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int decrementLikeCount(@Param("id") Long id);

    @Query("""
            update Post p
            set p.commentCount = p.commentCount + 1
            where p.id = :id
            """)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int incrementCommentCount(@Param("id") Long id);

    @Query("""
            update Post p
            set p.commentCount = p.commentCount - 1
            where p.id = :id and p.commentCount > 0
            """)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    int decrementCommentCount(@Param("id") Long id);

    @Query("""
            update Post p
            set p.commentCount =
                case
                    when p.commentCount + :delta < 0 then 0
                    else p.commentCount + :delta
                end
            where p.id = :id
            """)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int applyCommentCountDelta(@Param("id") Long id, @Param("delta") long delta);

    @Query("""
            select p.id from Post p where p.member.id = :memberId
            """)
    List<Long> findIdsByMemberId(@Param("memberId") Long memberId);

    @Query("""
            select p.member.id from Post p where p.id = :postId
            """)
    java.util.Optional<Long> findMemberIdByPostId(@Param("postId") Long postId);

    @Query("""
            select p from Post p
            join p.member m
            where m.accountStatus = :status
              and p.content like concat(:query, '%') escape '\\'
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
              and p.content like concat(:query, '%') escape '\\'
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
              and t.name like concat(:query, '%') escape '\\'
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
              and t.name like concat(:query, '%') escape '\\'
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

    @Query("""
            select p.id as id, i.imageObjectKey as imageObjectKey, p.createdAt as createdAt
            from Post p
            join p.member m
            join p.images i
            where m.accountStatus = :status
              and i.sortOrder = 1
              and p.content like concat(:query, '%') escape '\\'
            order by p.createdAt desc, p.id desc
            """)
    List<PostSummaryProjection> searchLatestByContentSummary(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
            select p.id as id, i.imageObjectKey as imageObjectKey, p.createdAt as createdAt
            from Post p
            join p.member m
            join p.images i
            where m.accountStatus = :status
              and i.sortOrder = 1
              and p.content like concat(:query, '%') escape '\\'
              and ((p.createdAt < :createdAt) or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<PostSummaryProjection> searchPageAfterByContentSummary(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select distinct p.id as id, i.imageObjectKey as imageObjectKey, p.createdAt as createdAt
            from Post p
            join p.member m
            join p.images i
            join p.postTags pt
            join pt.tag t
            where m.accountStatus = :status
              and i.sortOrder = 1
              and t.name like concat(:query, '%') escape '\\'
            order by p.createdAt desc, p.id desc
            """)
    List<PostSummaryProjection> searchLatestByTagSummary(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
            select distinct p.id as id, i.imageObjectKey as imageObjectKey, p.createdAt as createdAt
            from Post p
            join p.member m
            join p.images i
            join p.postTags pt
            join pt.tag t
            where m.accountStatus = :status
              and i.sortOrder = 1
              and t.name like concat(:query, '%') escape '\\'
              and ((p.createdAt < :createdAt) or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<PostSummaryProjection> searchPageAfterByTagSummary(
            @Param("query") String query,
            @Param("status") AccountStatus status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query(value = """
            select p.id as id, i.image_object_key as imageObjectKey, p.created_at as createdAt
            from posts p
            join members m on m.id = p.member_id
            join post_images i on i.post_id = p.id and i.sort_order = 1
            where m.account_status = :status
              and match(p.content) against(:query in natural language mode)
            order by match(p.content) against(:query in natural language mode) desc,
                     p.created_at desc,
                     p.id desc
            limit :size
            """, nativeQuery = true)
    List<PostSummaryProjection> searchLatestByContentFulltextSummary(
            @Param("query") String query,
            @Param("status") String status,
            @Param("size") int size
    );

    @Query("""
            select p.id from Post p
            join p.member m
            where m.accountStatus = :status
              and m.id in :memberIds
            order by p.createdAt desc, p.id desc
            """)
    List<Long> findFeedPostIdsLatest(
            @Param("memberIds") List<Long> memberIds,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
            select p.id from Post p
            join p.member m
            where m.accountStatus = :status
              and m.id in :memberIds
              and ((p.createdAt < :createdAt) or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<Long> findFeedPostIdsPageAfter(
            @Param("memberIds") List<Long> memberIds,
            @Param("status") AccountStatus status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select distinct p from Post p
            join fetch p.member m
            left join fetch p.images i
            where p.id in :postIds
            """)
    List<Post> findFeedDetailsByPostIds(@Param("postIds") Set<Long> postIds);
}
