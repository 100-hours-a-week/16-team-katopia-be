package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.Docs;

public record MemberFollowResponse(
        @Schema(description = Docs.FOLLOW_STATUS, example = "true")
        boolean isFollowing,
        @Schema(description = Docs.ID_DES, example = "1")
        Long targetId,
        @Schema(description = Docs.NICKNAME_DES, example = Docs.NICKNAME)
        String targetNickname,
        @Schema(description = Docs.AGGREGATE_DES)
        FollowAggregate aggregate
) {
    public static MemberFollowResponse of(Member target, boolean isFollowing) {
        return new MemberFollowResponse(
                isFollowing,
                target.getId(),
                target.getNickname(),
                new FollowAggregate(target.getFollowerCount(), target.getFollowingCount())
        );
    }

    public record FollowAggregate(
            @Schema(description = Docs.FOLLOWER_COUNT_DES, example = "0")
            long followerCount,
            @Schema(description = Docs.FOLLOWING_COUNT_DES, example = "0")
            long followingCount
    ) { }
}
