package katopia.fitcheck.service.post;

import katopia.fitcheck.dto.post.request.PostCreateRequest;
import katopia.fitcheck.dto.post.response.PostCreateResponse;
import katopia.fitcheck.dto.post.response.PostDetailResponse;
import katopia.fitcheck.dto.post.response.PostLikeResponse;
import katopia.fitcheck.dto.post.response.PostListResponse;
import katopia.fitcheck.dto.post.request.PostUpdateRequest;
import katopia.fitcheck.dto.post.response.PostUpdateResponse;
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
