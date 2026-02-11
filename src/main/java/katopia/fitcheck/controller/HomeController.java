package katopia.fitcheck.controller;

import katopia.fitcheck.dto.post.response.PostResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.PostSuccessCode;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.service.post.PostService;
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
public class HomeController {

    private final PostService postService;
    private final SecuritySupport securitySupport;

    @GetMapping("/posts")
    public ResponseEntity<APIResponse<PostResponse>> listHomePosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        PostResponse body = postService.listHomeFeed(memberId, size, after);
        return APIResponse.ok(PostSuccessCode.POST_LISTED, body);
    }
}
