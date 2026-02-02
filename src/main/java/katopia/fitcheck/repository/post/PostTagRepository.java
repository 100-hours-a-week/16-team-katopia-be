package katopia.fitcheck.repository.post;

import katopia.fitcheck.domain.post.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    @Modifying
    @Query("delete from PostTag pt where pt.post.id = :postId")
    void deleteByPostId(Long postId);

    @Query("""
            select t.name from PostTag pt
            join pt.tag t
            where pt.post.id = :postId
            order by t.id asc
            """)
    List<String> findTagNamesByPostId(@Param("postId") Long postId);
}
