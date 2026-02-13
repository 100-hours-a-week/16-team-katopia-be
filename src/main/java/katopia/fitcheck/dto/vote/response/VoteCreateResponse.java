package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.util.List;

@Builder
public record VoteCreateResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.VOTE_TITLE_DES, example = Docs.VOTE_TITLE)
        String title,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_LIST_DES, example = Docs.IMAGE_OBJECT_KEY_LIST)
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
