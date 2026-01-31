package katopia.fitcheck.dto.member.response;

public record MemberAggregate(
    int PostCount,
    int FollowingCount,
    int FollowerCount
) { }
