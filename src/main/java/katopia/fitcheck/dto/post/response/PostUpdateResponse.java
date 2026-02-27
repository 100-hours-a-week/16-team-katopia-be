package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostUpdateResponse(
        @Schema(description = Docs.POST_CONTENT_UPDATE_DES, example = Docs.POST_CONTENT_UPDATE)
        String content,
        @Schema(description = Docs.UPDATED_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime updatedAt
) {
    public static PostUpdateResponse of(Post post) {
        return PostUpdateResponse.builder()
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
