package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailResponse(
        List<PostImage> imageObjectKeys,
        String content,
        List<String> tags,
        boolean isLiked,
        PostAuthorResponse author,
        PostAggregateResponse aggregate,
        LocalDateTime createdAt
) {
    public static PostDetailResponse of (Post post, Member author, List<String> tags, boolean isLiked) {
        return PostDetailResponse.builder()
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
