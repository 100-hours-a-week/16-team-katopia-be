package katopia.fitcheck.repository.post;

import katopia.fitcheck.domain.post.PostBookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    Optional<PostBookmark> findByMemberIdAndPostId(Long memberId, Long postId);

    void deleteByPostId(Long postId);

    @Query("""
            select p.id as id, i.imageObjectKey as imageObjectKey, p.createdAt as createdAt
            from PostBookmark b
            join b.post p
            join p.images i
            where b.member.id = :memberId
              and i.sortOrder = 1
            order by p.createdAt desc, p.id desc
            """)
    List<PostSummaryProjection> findLatestBookmarks(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query("""
            select p.id as id, i.imageObjectKey as imageObjectKey, p.createdAt as createdAt
            from PostBookmark b
            join b.post p
            join p.images i
            where b.member.id = :memberId
              and i.sortOrder = 1
              and ((p.createdAt < :createdAt) or (p.createdAt = :createdAt and p.id < :id))
            order by p.createdAt desc, p.id desc
            """)
    List<PostSummaryProjection> findBookmarksPageAfter(
            @Param("memberId") Long memberId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
            select b.post.id from PostBookmark b
            where b.member.id = :memberId
              and b.post.id in :postIds
            """)
    Set<Long> findBookmarkedPostIds(
            @Param("memberId") Long memberId,
            @Param("postIds") List<Long> postIds
    );
}
