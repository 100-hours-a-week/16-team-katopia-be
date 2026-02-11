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

    @Modifying
    @Query("delete from PostTag pt where pt.post.id = :postId and pt.tag.id in :tagIds")
    void deleteByPostIdAndTagIds(
            @Param("postId") Long postId,
            @Param("tagIds") java.util.Set<Long> tagIds
    );

    @Query("""
            select t.name from PostTag pt
            join pt.tag t
            where pt.post.id = :postId
            order by t.id asc
            """)
    List<String> findTagNamesByPostId(@Param("postId") Long postId);

    @Query("""
            select pt.post.id as postId, t.name as name
            from PostTag pt
            join pt.tag t
            where pt.post.id in :postIds
            order by pt.post.id asc, t.id asc
            """)
    List<PostTagNameProjection> findTagNamesByPostIds(@Param("postIds") List<Long> postIds);
}
