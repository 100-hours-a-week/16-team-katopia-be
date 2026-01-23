package katopia.fitcheck.post.repository;

import katopia.fitcheck.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);
    void deleteByPostId(Long postId);
    void deleteByMemberId(Long memberId);
}
