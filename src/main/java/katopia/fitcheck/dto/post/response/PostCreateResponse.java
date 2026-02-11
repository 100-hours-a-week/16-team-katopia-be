package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.domain.post.Tag;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record PostCreateResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long id,
        @Schema(description = SwaggerExamples.POST_CONTENT_CREATE_DES, example = SwaggerExamples.POST_CONTENT_CREATE)
        String content,
        @Schema(description = "이미지 목록")
        List<PostImage> imageObjectKeys,
        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.TAG_LIST_DES, example = SwaggerExamples.TAG_LIST),
                schema = @Schema(description = SwaggerExamples.TAG_DES)
        )
        List<String> tags,
        @Schema(description = "작성 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
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
