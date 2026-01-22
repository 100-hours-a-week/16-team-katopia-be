package katopia.fitcheck.post.repository;

import katopia.fitcheck.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            select p from Post p
            order by p.createdAt desc, p.id desc
            """)
    List<Post> findLatest(Pageable pageable);

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
}
