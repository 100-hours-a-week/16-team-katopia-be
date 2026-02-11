package katopia.fitcheck.repository.post;

import katopia.fitcheck.domain.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);
    @Modifying
    @Query("delete from PostLike pl where pl.post.id = :postId")
    void deleteByPostId(Long postId);
    void deleteByMemberId(Long memberId);

    @Query("""
            select pl.post.id from PostLike pl
            where pl.member.id = :memberId
              and pl.post.id in :postIds
            """)
    Set<Long> findLikedPostIds(
            Long memberId,
            List<Long> postIds
    );
}
