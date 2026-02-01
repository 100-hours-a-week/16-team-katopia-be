package katopia.fitcheck.dto.post.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostSummary(
        Long id,
        String imageObjectKey,
        LocalDateTime createdAt
) { }
