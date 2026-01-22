package katopia.fitcheck.post.dto;

import lombok.Builder;

@Builder
public record PostLikeResponse(
        Long postId,
        long likeCount
) { }
