package katopia.fitcheck.repository.post;

import java.time.LocalDateTime;

public interface PostSummaryProjection {
    Long getId();
    String getImageObjectKey();
    LocalDateTime getCreatedAt();
}
