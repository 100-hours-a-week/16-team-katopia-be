package katopia.fitcheck.post.dto;

import katopia.fitcheck.member.domain.Member;
import katopia.fitcheck.post.domain.Post;
import katopia.fitcheck.post.domain.PostImage;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailResponse(
        List<PostImage> imageUrls,
        String content,
        List<String> tags,
        PostAuthorResponse author,
        PostAggregateResponse aggregate,
        LocalDateTime createdAt
) {
    public static PostDetailResponse of (Post post, Member author) {
        return PostDetailResponse.builder()
                .imageUrls(post.getImageUrls())
                .content(post.getContent())
                .tags(post.getPostTags().stream()
                        .map(postTag -> postTag.getTag().getName())
                        .distinct()
                        .toList())
                .author(PostAuthorResponse.of(author))
                .aggregate(PostAggregateResponse.of(post))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
