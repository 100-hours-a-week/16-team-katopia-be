package katopia.fitcheck.comment.repository;

import katopia.fitcheck.comment.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select c from Comment c
            join fetch c.member
            where c.post.id = :postId
            order by c.createdAt desc, c.id desc
            """)
    List<Comment> findLatestByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("""
            select c from Comment c
            join fetch c.member
            where c.post.id = :postId
              and ((c.createdAt < :createdAt)
               or (c.createdAt = :createdAt and c.id < :id))
            order by c.createdAt desc, c.id desc
            """)
    List<Comment> findPageAfter(
            @Param("postId") Long postId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    Optional<Comment> findByIdAndPostId(Long id, Long postId);
}
