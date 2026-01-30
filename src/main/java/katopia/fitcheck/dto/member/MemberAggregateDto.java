package katopia.fitcheck.dto.member;

public record MemberAggregateDto(
    int PostCount,
    int FollowingCount,
    int FollowerCount
) { }
