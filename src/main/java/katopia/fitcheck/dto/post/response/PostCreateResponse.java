package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.domain.post.Tag;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record PostCreateResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.POST_CONTENT_DES, example = Docs.POST_CONTENT)
        String content,
        @Schema(description = Docs.POST_IMAGE_LIST_DES)
        List<PostImage> imageObjectKeys,
        @ArraySchema(
                arraySchema = @Schema(description = Policy.TAG_LIST_DES, example = Docs.TAG_LIST),
                schema = @Schema(description = Docs.TAG_DES)
        )
        List<String> tags,
        @Schema(description = Docs.CREATED_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime createdAt
) {
    public static PostCreateResponse of(Post post, Set<Tag> tags) {
        return PostCreateResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageObjectKeys(post.getImages())
                .tags(mapTagNames(tags))
                .createdAt(post.getCreatedAt())
                .build();
    }

    private static List<String> mapTagNames(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
