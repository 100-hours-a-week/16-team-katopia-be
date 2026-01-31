package katopia.fitcheck.repository.post;

import katopia.fitcheck.domain.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);
    void deleteByPostId(Long postId);
    void deleteByMemberId(Long memberId);
}
