package katopia.fitcheck.dto.vote.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.util.List;

@Builder
public record VoteCreateResponse(
        @Schema(description = "투표 ID", example = SwaggerExamples.VOTE_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.VOTE_TITLE_DES, example = SwaggerExamples.VOTE_TITLE)
        String title,
        @Schema(description = SwaggerExamples.IMAGE_OBJECT_KEY_LIST_DES, example = SwaggerExamples.VOTE_IMAGE_OBJECT_KEY_LIST)
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
