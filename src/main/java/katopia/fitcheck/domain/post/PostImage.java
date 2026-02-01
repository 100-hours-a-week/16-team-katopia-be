package katopia.fitcheck.domain.post;

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

    @Column(name = "image_object_key", length = 1024, nullable = false)
    private String imageObjectKey;

    private PostImage(int sortOrder, String imageObjectKey) {
        this.sortOrder = sortOrder;
        this.imageObjectKey = imageObjectKey;
    }

    public static PostImage of(int sortOrder, String imageObjectKey) {
        return new PostImage(sortOrder, imageObjectKey);
    }
}
