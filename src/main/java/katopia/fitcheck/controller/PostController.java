package katopia.fitcheck.controller;

import katopia.fitcheck.controller.spec.PostApiSpec;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.PostLikeSuccessCode;
import katopia.fitcheck.global.exception.code.PostSuccessCode;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.post.dto.PostCreateRequest;
import katopia.fitcheck.post.dto.PostCreateResponse;
import katopia.fitcheck.post.dto.PostDetailResponse;
import katopia.fitcheck.post.dto.PostLikeResponse;
import katopia.fitcheck.post.dto.PostListResponse;
import katopia.fitcheck.post.dto.PostUpdateRequest;
import katopia.fitcheck.post.dto.PostUpdateResponse;
import katopia.fitcheck.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController implements PostApiSpec {

    private final PostService postService;
    private final SecuritySupport securitySupport;

    @PostMapping
    @Override
    public ResponseEntity<APIResponse<PostCreateResponse>> createPost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody PostCreateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        PostCreateResponse response = postService.create(memberId, request);

        return APIResponse.ok(PostSuccessCode.POST_CREATED, response);
    }


    @GetMapping
    @Override
    public ResponseEntity<APIResponse<PostListResponse>> listPosts(
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        PostListResponse body = postService.list(size, after);
        return APIResponse.ok(PostSuccessCode.POST_LISTED, body);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<APIResponse<PostDetailResponse>> getPost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        PostDetailResponse body = postService.getDetail(memberId, id);
        return APIResponse.ok(PostSuccessCode.POST_FETCHED, body);
    }


    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<APIResponse<PostUpdateResponse>> updatePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id,
            @RequestBody PostUpdateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        PostUpdateResponse body = postService.update(memberId, id, request);
        return APIResponse.ok(PostSuccessCode.POST_UPDATED, body);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        postService.delete(memberId, id);
        return APIResponse.noContent(PostSuccessCode.POST_DELETED);
    }

    @PostMapping("/{id}/likes")
    @Override
    public ResponseEntity<APIResponse<PostLikeResponse>> likePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        PostLikeResponse body = postService.like(memberId, id);
        return APIResponse.ok(PostLikeSuccessCode.POST_LIKED, body);
    }

    @DeleteMapping("/{id}/likes")
    @Override
    public ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long id
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        postService.unlike(memberId, id);
        return APIResponse.noContent(PostLikeSuccessCode.POST_UNLIKED);
    }
}
