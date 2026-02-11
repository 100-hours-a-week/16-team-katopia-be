package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long id,
        @Schema(description = "이미지 목록")
        List<PostImage> imageObjectKeys,
        @Schema(description = SwaggerExamples.POST_CONTENT_CREATE_DES, example = SwaggerExamples.POST_CONTENT_CREATE)
        String content,
        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.TAG_LIST_DES, example = SwaggerExamples.TAG_LIST),
                schema = @Schema(description = SwaggerExamples.TAG_DES)
        )
        List<String> tags,
        @Schema(description = "좋아요 여부", example = "false")
        boolean isLiked,
        @Schema(description = "작성자 정보")
        PostAuthorResponse author,
        @Schema(description = "게시글 집계")
        PostAggregateResponse aggregate,
        @Schema(description = "작성 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
        LocalDateTime createdAt
) {
    public static PostDetailResponse of (Post post, Member author, List<String> tags, boolean isLiked) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .imageObjectKeys(post.getImages())
                .content(post.getContent())
                .tags(tags)
                .isLiked(isLiked)
                .author(PostAuthorResponse.of(author))
                .aggregate(PostAggregateResponse.of(post))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
