package katopia.fitcheck.dto.dev.response;

public record DevDummyResponse(
        int createdCount,
        Long followId
) {
    public static DevDummyResponse of(int createdCount, Long followId) {
        return new DevDummyResponse(createdCount, followId);
    }
}
