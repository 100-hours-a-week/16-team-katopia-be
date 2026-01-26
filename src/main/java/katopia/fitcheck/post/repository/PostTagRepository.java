package katopia.fitcheck.post.repository;

import katopia.fitcheck.post.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    void deleteByPostId(Long postId);

    @Query("""
            select t.name from PostTag pt
            join pt.tag t
            where pt.post.id = :postId
            order by t.id asc
            """)
    List<String> findTagNamesByPostId(@Param("postId") Long postId);
}
