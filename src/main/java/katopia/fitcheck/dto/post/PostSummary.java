package katopia.fitcheck.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostSummary(
        Long id,
        String imageUrls,
        LocalDateTime createdAt
) { }
