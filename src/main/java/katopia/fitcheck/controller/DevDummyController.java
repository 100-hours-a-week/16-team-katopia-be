package katopia.fitcheck.controller;

import katopia.fitcheck.dto.dev.response.DevDummyResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.CommonSuccessCode;
import katopia.fitcheck.service.dev.DevDummyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile({"local", "dev"})
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevDummyController {

    private final DevDummyService devDummyService;

    @PostMapping("/dummy")
    public ResponseEntity<APIResponse<DevDummyResponse>> createDummyFollowers(
            @RequestParam("n") int count,
            @RequestParam("followId") Long followId
    ) {
        DevDummyResponse response = devDummyService.createDummyFollowers(count, followId);
        return APIResponse.ok(CommonSuccessCode.DUMMY_CREATED, response);
    }
}
