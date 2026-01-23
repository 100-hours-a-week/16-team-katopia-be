package katopia.fitcheck.post.service;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.PostLikeErrorCode;
import katopia.fitcheck.member.domain.Member;
import katopia.fitcheck.member.service.MemberFinder;
import katopia.fitcheck.post.domain.Post;
import katopia.fitcheck.post.domain.PostLike;
import katopia.fitcheck.post.dto.PostLikeResponse;
import katopia.fitcheck.post.repository.PostLikeRepository;
import katopia.fitcheck.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberFinder memberFinder;
    private final PostFinder postFinder;

    @Transactional
    public PostLikeResponse like(Long memberId, Long postId) {
        if (postLikeRepository.existsByMemberIdAndPostId(memberId, postId)) {
            throw new BusinessException(PostLikeErrorCode.ALREADY_LIKED);
        }
        postFinder.requireExists(postId);
        Post post = postFinder.getReferenceById(postId);
        Member member = memberFinder.findByIdOrThrow(memberId);
        PostLike like = PostLike.of(member, post);
        postLikeRepository.save(like);
        postRepository.incrementLikeCount(postId);
        long likeCount = resolveLikeCount(postId);
        return new PostLikeResponse(post.getId(), likeCount);
    }

    @Transactional
    public void unlike(Long memberId, Long postId) {
        PostLike like = postLikeRepository.findByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new BusinessException(PostLikeErrorCode.NOT_FOUND_LIKE));
        Post post = like.getPost();
        postLikeRepository.delete(like);
        postRepository.decrementLikeCount(post.getId());
        resolveLikeCount(post.getId());
    }

    private long resolveLikeCount(Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);
        return post.getLikeCount();
    }
}
