package katopia.fitcheck.controller;

import katopia.fitcheck.controller.spec.SearchApiSpec;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.CommonSuccessCode;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.search.dto.PostSearchResponse;
import katopia.fitcheck.search.dto.MemberSearchResponse;
import katopia.fitcheck.search.service.SearchService;
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
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after,
            @RequestParam(value = "height", required = false) String height,
            @RequestParam(value = "weight", required = false) String weight,
            @RequestParam(value = "gender", required = false) String gender
    ) {
        Long memberId = securitySupport.findMemberIdOrNull(principal);
        MemberSearchResponse body = searchService.searchUsers(memberId, query, size, after, height, weight, gender);
        return APIResponse.ok(CommonSuccessCode.SEARCH_COMPLETED, body);
    }

    @GetMapping("/posts")
    @Override
    public ResponseEntity<APIResponse<PostSearchResponse>> searchPosts(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after,
            @RequestParam(value = "height", required = false) String height,
            @RequestParam(value = "weight", required = false) String weight,
            @RequestParam(value = "gender", required = false) String gender
    ) {
        Long memberId = securitySupport.findMemberIdOrNull(principal);
        PostSearchResponse body = searchService.searchPosts(memberId, query, size, after, height, weight, gender);
        return APIResponse.ok(CommonSuccessCode.SEARCH_COMPLETED, body);
    }
}
