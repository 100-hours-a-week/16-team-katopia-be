package katopia.fitcheck.domain.post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(
        name = "posts",
        indexes = {
                @Index(name = "idx_posts_member_created", columnList = "member_id, created_at"),
                @Index(name = "idx_posts_created", columnList = "created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 200, nullable = false)
    private String content;

    @Column(name = "like_count", nullable = false, columnDefinition = "bigint default 0")
    private long likeCount;

    @Column(name = "comment_count", nullable = false, columnDefinition = "bigint default 0")
    private long commentCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_images",
            joinColumns = @JoinColumn(name = "post_id"),
            uniqueConstraints = {
                    @UniqueConstraint(name = "uk_post_images_post_order", columnNames = {"post_id", "sort_order"})
            },
            indexes = {
                    @Index(name = "idx_post_images_order", columnList = "post_id, sort_order")
            }
    )
    @OrderBy("sortOrder ASC")
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostTag> postTags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> postLikes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    private Post(Member member,
                 String content,
                 long likeCount,
                 long commentCount,
                 List<PostImage> images,
                 Set<PostTag> postTags
    ) {
        this.member = member;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        if (images != null) {
            this.images.addAll(images);
        }
        if (postTags != null) {
            this.postTags.addAll(postTags);
        }
    }

    public static Post create(Member member, String content, List<PostImage> images) {
        return Post.builder()
                .member(member)
                .content(content)
                .likeCount(0L)
                .commentCount(0L)
                .images(images)
                .postTags(Set.of())
                .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void replaceImages(List<PostImage> images) {
        this.images.clear();
        if (images != null) {
            this.images.addAll(images);
        }
    }

    public void replaceTags(Set<PostTag> tags) {
        this.postTags.clear();
        if (tags != null) {
            this.postTags.addAll(tags);
        }
    }

    public List<PostImage> getImageObjectKeys() {
        if (images == null) {
            return Collections.emptyList();
        }
        return images;
    }
}
