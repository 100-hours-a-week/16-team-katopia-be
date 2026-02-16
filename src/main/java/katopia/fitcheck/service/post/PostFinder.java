package katopia.fitcheck.service.post;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostFinder {

    private final PostRepository postRepository;

    public Post findByIdOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
    }

    public Post findDetailByIdOrThrow(Long postId) {
        return postRepository.findDetailById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
    }

    public void requireExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(PostErrorCode.POST_NOT_FOUND);
        }
    }

    public Post getReferenceById(Long postId) {
        return postRepository.getReferenceById(postId);
    }

    public Long findMemberIdByPostIdOrThrow(Long postId) {
        return postRepository.findMemberIdByPostId(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
    }
}
