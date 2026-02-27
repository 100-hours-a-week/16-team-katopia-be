package katopia.fitcheck.controller;

import katopia.fitcheck.controller.spec.HomeApiSpec;
import katopia.fitcheck.dto.post.response.PostResponse;
import katopia.fitcheck.dto.recommendation.RecommendationResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.CommonSuccessCode;
import katopia.fitcheck.global.exception.code.PostSuccessCode;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.service.post.PostService;
import katopia.fitcheck.service.recommendation.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController implements HomeApiSpec {

    private final PostService postService;
    private final RecommendationService recommendationService;
    private final SecuritySupport securitySupport;

    @GetMapping("/posts")
    @Override
    public ResponseEntity<APIResponse<PostResponse>> listHomePosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        PostResponse body = postService.listHomeFeed(memberId, size, after);
        return APIResponse.ok(PostSuccessCode.POST_LISTED, body);
    }

    @GetMapping("/members")
    @Override
    public ResponseEntity<APIResponse<RecommendationResponse>> listHomeMembers(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        RecommendationResponse body = recommendationService.recommendUsers(memberId);
        return APIResponse.ok(CommonSuccessCode.SEARCH_COMPLETED, body);
    }
}
