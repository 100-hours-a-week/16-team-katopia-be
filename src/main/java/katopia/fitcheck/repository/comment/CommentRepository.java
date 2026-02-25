package katopia.fitcheck.repository.comment;

import katopia.fitcheck.domain.comment.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
              and c.deletedAt is null
            order by c.createdAt desc, c.id desc
            """)
    List<Comment> findLatestByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("""
            select c from Comment c
            join fetch c.member
            where c.post.id = :postId
              and c.deletedAt is null
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

    Optional<Comment> findByIdAndPostIdAndDeletedAtIsNull(Long id, Long postId);

    @Modifying
    @Query("update Comment c set c.deletedAt = :deletedAt where c.post.id = :postId and c.deletedAt is null")
    int softDeleteByPostId(@Param("postId") Long postId, @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying
    @Query("delete from Comment c where c.post.id = :postId")
    int deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("update Comment c set c.deletedAt = :deletedAt where c.member.id = :memberId and c.deletedAt is null")
    int softDeleteByMemberId(@Param("memberId") Long memberId, @Param("deletedAt") LocalDateTime deletedAt);
}
