package katopia.fitcheck.service.post;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.domain.post.PostTag;
import katopia.fitcheck.domain.post.Tag;
import katopia.fitcheck.dto.post.request.PostCreateRequest;
import katopia.fitcheck.dto.post.response.PostCreateResponse;
import katopia.fitcheck.dto.post.request.PostUpdateRequest;
import katopia.fitcheck.dto.post.response.PostUpdateResponse;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.repository.post.PostLikeRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.repository.post.PostBookmarkRepository;
import katopia.fitcheck.repository.post.PostTagRepository;
import katopia.fitcheck.repository.post.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommandService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostValidator postValidator;
    private final MemberFinder memberFinder;
    private final PostFinder postFinder;

    @Transactional
    public PostCreateResponse create(Long memberId, PostCreateRequest request) {
        Member proxyMember = memberFinder.getReferenceById(memberId);
        String content = normalizeContent(request.content());
        List<String> imageObjectKeys = normalizeImages(request.imageObjectKeys());
        List<String> tags = normalizeTags(request.tags());

        List<PostImage> images = toImages(imageObjectKeys);
        Set<Tag> tagEntities = resolveTags(tags);
        Post post = Post.create(proxyMember, content, images);
        post.replaceTags(buildPostTags(post, tagEntities));

        Post saved = postRepository.save(post);
        // TODO: 게시글 수 집계는 Redis 기준으로 처리 후 비동기 DB 동기화로 전환
        memberRepository.incrementPostCount(memberId);
        return PostCreateResponse.of(saved, tagEntities);
    }

    @Transactional
    public PostUpdateResponse update(Long memberId, Long postId, PostUpdateRequest request) {
        Post post = postFinder.findByIdOrThrow(postId);
        postValidator.validateOwner(post, memberId);

        String content = normalizeContent(request.content());
        List<String> tags = normalizeTags(request.tags());

        post.updateContent(content);
        syncTags(post, resolveTags(tags));

        return PostUpdateResponse.of(post);
    }

    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);
        postValidator.validateOwner(post, memberId);
        // TODO: 게시글 수 집계는 Redis 기준으로 처리 후 비동기 DB 동기화로 전환
        memberRepository.decrementPostCount(memberId);
        commentRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
        postBookmarkRepository.deleteByPostId(postId);
        postTagRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    private List<PostImage> toImages(List<String> imageObjectKeys) {
        List<PostImage> images = new ArrayList<>();
        int order = 1;
        for (String objectKey : imageObjectKeys) {
            images.add(PostImage.of(order, objectKey));
            order += 1;
        }
        return images;
    }

    private String normalizeContent(String content) {
        return content == null ? null : content.trim();
    }

    private List<String> normalizeImages(List<String> imageObjectKeys) {
        if (imageObjectKeys == null) {
            return List.of();
        }
        return imageObjectKeys.stream().map(String::trim).toList();
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .map(this::normalizeTag)
                .toList();
    }

    private String normalizeTag(String tag) {
        String trimmed = tag.trim();
        if (trimmed.startsWith("#")) {
            trimmed = trimmed.substring(1).trim();
        }
        return trimmed;
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

        Set<Long> removedTagIds = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getId())
                .filter(existingId -> !newTagIds.contains(existingId))
                .collect(Collectors.toSet());
        if (!removedTagIds.isEmpty()) {
            postTagRepository.deleteByPostIdAndTagIds(post.getId(), removedTagIds);
        }
        post.getPostTags().removeIf(postTag -> removedTagIds.contains(postTag.getTag().getId()));

        Set<Long> existingTagIds = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getId())
                .collect(Collectors.toSet());

        newTags.stream()
                .filter(tag -> !existingTagIds.contains(tag.getId()))
                .forEach(tag -> post.getPostTags().add(PostTag.of(post, tag)));
    }
}
