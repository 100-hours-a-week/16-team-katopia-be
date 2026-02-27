package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;

public record MemberAggregate(
        @Schema(description = Docs.POST_COUNT_DES, example = "0")
        long postCount,
        @Schema(description = Docs.FOLLOWER_COUNT_DES, example = "0")
        long followingCount,
        @Schema(description = Docs.FOLLOWING_COUNT_DES, example = "0")
        long followerCount
) { }
