package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberAggregate(
        @Schema(description = "게시글 수", example = "10")
        long postCount,
        @Schema(description = "팔로잉 수", example = "3")
        long followingCount,
        @Schema(description = "팔로워 수", example = "5")
        long followerCount
) { }
