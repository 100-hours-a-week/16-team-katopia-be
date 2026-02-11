package katopia.fitcheck.dto.member.response;

public record MemberAggregate(
    long postCount,
    long followingCount,
    long followerCount
) { }
