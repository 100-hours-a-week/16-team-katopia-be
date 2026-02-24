package katopia.fitcheck.repository.aggregation;

import katopia.fitcheck.domain.aggregation.CountBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountBatchRepository extends JpaRepository<CountBatch, Long> {
    Optional<CountBatch> findTopByOrderByIdDesc();
}
