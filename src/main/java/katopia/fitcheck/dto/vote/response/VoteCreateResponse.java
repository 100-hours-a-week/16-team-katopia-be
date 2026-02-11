package katopia.fitcheck.dto.vote.response;

import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import lombok.Builder;

import java.util.List;

@Builder
public record VoteCreateResponse(
        Long id,
        String title,
        List<String> imageObjectKeys
) {
    public static VoteCreateResponse of(Vote vote) {
        return VoteCreateResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .imageObjectKeys(mapImageKeys(vote.getItems()))
                .build();
    }

    private static List<String> mapImageKeys(List<VoteItem> items) {
        return items.stream()
                .map(VoteItem::getImageObjectKey)
                .toList();
    }
}
