package katopia.fitcheck.dto.post.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailResponse(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Docs.POST_IMAGE_LIST_DES)
        List<PostImage> imageObjectKeys,
        @Schema(description = Docs.POST_CONTENT_DES, example = Docs.POST_CONTENT)
        String content,
        @ArraySchema(
                arraySchema = @Schema(description = Policy.TAG_LIST_DES, example = Docs.TAG_LIST),
                schema = @Schema(description = Docs.TAG_DES)
        )
        List<String> tags,
        @Schema(description = "게시글 좋아요 여부", example = "false")
        boolean isLiked,
        @Schema(description = "게시글 북마크 여부", example = "false")
        boolean isBookmarked,
        @Schema(description = Docs.AUTHOR_DES)
        PostAuthorResponse author,
        @Schema(description = Docs.AGGREGATE_DES)
        PostAggregateResponse aggregate,
        @Schema(description = Docs.CREATED_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime createdAt
) {
    public static PostDetailResponse of (Post post, Member author, List<String> tags, boolean isLiked, boolean isBookmarked) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .imageObjectKeys(post.getImages())
                .content(post.getContent())
                .tags(tags)
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .author(PostAuthorResponse.of(author))
                .aggregate(PostAggregateResponse.of(post))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
