package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostSummary(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String imageObjectKey,
        @Schema(description = Docs.CREATED_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime createdAt
) {
    public static PostSummary of(Long id, String imageObjectKey, LocalDateTime createdAt) {
        return PostSummary.builder()
                .id(id)
                .imageObjectKey(imageObjectKey)
                .createdAt(createdAt)
                .build();
    }
}
