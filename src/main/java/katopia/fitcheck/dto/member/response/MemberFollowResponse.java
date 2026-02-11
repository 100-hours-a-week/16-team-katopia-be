package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.SwaggerExamples;

public record MemberFollowResponse(
        @Schema(description = "팔로우 상태", example = "true")
        boolean isFollowing,
        @Schema(description = SwaggerExamples.MEMBER_ID_DES, example = SwaggerExamples.MEMBER_ID_EXAMPLE)
        Long targetId,
        @Schema(description = "대상 닉네임", example = SwaggerExamples.NICKNAME)
        String targetNickname,
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
            @Schema(description = "팔로워 수", example = "12")
            long followerCount,
            @Schema(description = "팔로잉 수", example = "5")
            long followingCount
    ) { }
}
