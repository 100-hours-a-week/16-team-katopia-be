package katopia.fitcheck.dto.search;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberSearchResponse(
        List<MemberSummary> members,
        String nextCursor
) implements SearchResultCount {
    public static MemberSearchResponse of(List<MemberSummary> members, String nextCursor) {
        return MemberSearchResponse.builder()
                .members(members)
                .nextCursor(nextCursor)
                .build();
    }

    @Override
    public int resultCount() {
        return members == null ? 0 : members.size();
    }
}
