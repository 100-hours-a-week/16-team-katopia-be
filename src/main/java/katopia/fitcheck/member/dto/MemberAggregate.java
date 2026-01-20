package katopia.fitcheck.member.dto;

public record MemberAggregate(
    int PostCount,
    int FollowingCount,
    int FollowerCount
) { }
