package katopia.fitcheck.dto.post.response;

import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.domain.post.Tag;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record PostCreateResponse(
        Long id,
        String content,
        List<PostImage> imageObjectKeys,
        List<String> tags,
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
