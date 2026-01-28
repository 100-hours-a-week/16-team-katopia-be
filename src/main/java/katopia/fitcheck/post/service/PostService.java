package katopia.fitcheck.post.service;

import katopia.fitcheck.post.dto.PostCreateRequest;
import katopia.fitcheck.post.dto.PostCreateResponse;
import katopia.fitcheck.post.dto.PostDetailResponse;
import katopia.fitcheck.post.dto.PostLikeResponse;
import katopia.fitcheck.post.dto.PostListResponse;
import katopia.fitcheck.post.dto.PostUpdateRequest;
import katopia.fitcheck.post.dto.PostUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCommandService postCommandService;
    private final PostSearchService postSearchService;
    private final PostLikeService postLikeService;

    public PostCreateResponse create(Long memberId, PostCreateRequest request) {
        return postCommandService.create(memberId, request);
    }

    public PostListResponse list(String sizeValue, String after) {
        return postSearchService.list(sizeValue, after);
    }

    public PostListResponse listByMember(Long memberId, String sizeValue, String after) {
        return postSearchService.listByMember(memberId, sizeValue, after);
    }

    public PostDetailResponse getDetail(Long memberId, Long postId) {
        return postSearchService.getDetail(memberId, postId);
    }

    public PostUpdateResponse update(Long memberId, Long postId, PostUpdateRequest request) {
        return postCommandService.update(memberId, postId, request);
    }

    public void delete(Long memberId, Long postId) {
        postCommandService.delete(memberId, postId);
    }

    public PostLikeResponse like(Long memberId, Long postId) {
        return postLikeService.like(memberId, postId);
    }

    public void unlike(Long memberId, Long postId) {
        postLikeService.unlike(memberId, postId);
    }
}
