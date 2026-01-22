package katopia.fitcheck.member.dto;

public record MemberAggregateDto(
    int PostCount,
    int FollowingCount,
    int FollowerCount
) { }
