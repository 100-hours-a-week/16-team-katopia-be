package katopia.fitcheck.post.service;

import katopia.fitcheck.member.domain.Member;
import katopia.fitcheck.member.service.MemberFinder;
import katopia.fitcheck.post.domain.Post;
import katopia.fitcheck.post.domain.PostImage;
import katopia.fitcheck.post.domain.PostTag;
import katopia.fitcheck.post.domain.Tag;
import katopia.fitcheck.post.dto.PostCreateRequest;
import katopia.fitcheck.post.dto.PostCreateResponse;
import katopia.fitcheck.post.dto.PostUpdateRequest;
import katopia.fitcheck.post.dto.PostUpdateResponse;
import katopia.fitcheck.post.repository.PostLikeRepository;
import katopia.fitcheck.post.repository.PostRepository;
import katopia.fitcheck.post.repository.PostTagRepository;
import katopia.fitcheck.post.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommandService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostValidator postValidator;
    private final MemberFinder memberFinder;
    private final PostFinder postFinder;

    @Transactional
    public PostCreateResponse create(Long memberId, PostCreateRequest request) {
        Member proxyMember = memberFinder.getReferenceById(memberId);
        String content = postValidator.validateContent(request.content());
        List<String> imageUrls = postValidator.validateImages(request.imageUrls());
        List<String> tags = postValidator.validateTags(request.tags());

        List<PostImage> images = toImages(imageUrls);
        Set<Tag> tagEntities = resolveTags(tags);
        Post post = Post.create(proxyMember, content, images);
        post.replaceTags(buildPostTags(post, tagEntities));

        Post saved = postRepository.save(post);
        return PostCreateResponse.of(saved);
    }

    @Transactional
    public PostUpdateResponse update(Long memberId, Long postId, PostUpdateRequest request) {
        Post post = postFinder.findByIdOrThrow(postId);
        postValidator.validateOwner(post, memberId);

        String content = postValidator.validateContent(request.content());
        List<String> imageUrls = postValidator.validateImages(request.imageUrls());
        List<String> tags = postValidator.validateTags(request.tags());

        post.updateContent(content);
        post.replaceImages(toImages(imageUrls));
        syncTags(post, resolveTags(tags));

        return PostUpdateResponse.of(post);
    }

    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);
        postValidator.validateOwner(post, memberId);
        postLikeRepository.deleteByPostId(postId);
        postTagRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    private List<PostImage> toImages(List<String> imageUrls) {
        List<PostImage> images = new ArrayList<>();
        int order = 1;
        for (String url : imageUrls) {
            images.add(PostImage.of(order, url));
            order += 1;
        }
        return images;
    }

    private Set<Tag> resolveTags(List<String> tags) {
        if (tags.isEmpty()) {
            return Set.of();
        }
        List<String> normalized = tags.stream().map(String::trim).toList();
        List<Tag> existing = tagRepository.findByNameIn(normalized);
        Set<String> existingNames = existing.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        List<Tag> toSave = normalized.stream()
                .filter(name -> !existingNames.contains(name))
                .distinct()
                .map(Tag::of).toList();
        if (!toSave.isEmpty()) {
            existing = new ArrayList<>(existing);
            existing.addAll(tagRepository.saveAll(toSave));
        }
        return new LinkedHashSet<>(existing);
    }

    private Set<PostTag> buildPostTags(Post post, Set<Tag> tags) {
        return tags.stream()
                .map(tag -> PostTag.of(post, tag))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void syncTags(Post post, Set<Tag> newTags) {
        Set<Long> newTagIds = newTags.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());

        post.getPostTags().removeIf(postTag -> !newTagIds.contains(postTag.getTag().getId()));

        Set<Long> existingTagIds = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getId())
                .collect(Collectors.toSet());

        newTags.stream()
                .filter(tag -> !existingTagIds.contains(tag.getId()))
                .forEach(tag -> post.getPostTags().add(PostTag.of(post, tag)));
    }
}
