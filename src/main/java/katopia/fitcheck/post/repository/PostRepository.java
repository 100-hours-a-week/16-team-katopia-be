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
}
