package katopia.fitcheck.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberSearchResponse(
        @Schema(description = "회원 검색 결과")
        List<MemberSearchSummary> members,
        @Schema(description = Docs.CURSOR_DES, example = Docs.CURSOR)
        String nextCursor
) implements SearchResultCount {
    public static MemberSearchResponse of(List<MemberSearchSummary> members, String nextCursor) {
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
