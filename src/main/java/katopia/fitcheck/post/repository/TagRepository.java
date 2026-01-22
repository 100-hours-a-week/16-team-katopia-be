package katopia.fitcheck.post.repository;

import katopia.fitcheck.post.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameIn(Collection<String> names);
}
