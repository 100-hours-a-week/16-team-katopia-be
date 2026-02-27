package katopia.fitcheck.controller;

import katopia.fitcheck.controller.spec.SearchApiSpec;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.CommonSuccessCode;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.dto.search.PostSearchResponse;
import katopia.fitcheck.dto.search.MemberSearchResponse;
import katopia.fitcheck.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController implements SearchApiSpec {

    private final SearchService searchService;
    private final SecuritySupport securitySupport;

    @GetMapping("/users")
    @Override
    public ResponseEntity<APIResponse<MemberSearchResponse>> searchUsers(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    ) {
        Long requesterId = securitySupport.requireMemberId(principal);
        MemberSearchResponse body = searchService.searchUsers(requesterId, query, size, after);
        return APIResponse.ok(CommonSuccessCode.SEARCH_COMPLETED, body);
    }

    @GetMapping("/posts")
    @Override
    public ResponseEntity<APIResponse<PostSearchResponse>> searchPosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    ) {
        securitySupport.requireMemberId(principal);
        PostSearchResponse body = searchService.searchPostsFulltext(query, size);
        return APIResponse.ok(CommonSuccessCode.SEARCH_COMPLETED, body);
    }
}
