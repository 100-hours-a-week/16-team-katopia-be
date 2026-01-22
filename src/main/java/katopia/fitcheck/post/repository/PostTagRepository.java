package katopia.fitcheck.post.repository;

import katopia.fitcheck.post.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    void deleteByPostId(Long postId);
}
