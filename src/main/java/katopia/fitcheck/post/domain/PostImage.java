package katopia.fitcheck.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "image_url", length = 1024, nullable = false)
    private String imageUrl;

    private PostImage(int sortOrder, String imageUrl) {
        this.sortOrder = sortOrder;
        this.imageUrl = imageUrl;
    }

    public static PostImage of(int sortOrder, String imageUrl) {
        return new PostImage(sortOrder, imageUrl);
    }
}
