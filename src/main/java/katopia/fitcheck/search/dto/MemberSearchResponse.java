package katopia.fitcheck.search.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberSearchResponse(
        List<MemberSummary> members,
        String nextCursor
) {
    public static MemberSearchResponse of(List<MemberSummary> members, String nextCursor) {
        return MemberSearchResponse.builder()
                .members(members)
                .nextCursor(nextCursor)
                .build();
    }
}
