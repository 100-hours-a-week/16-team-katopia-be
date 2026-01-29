package katopia.fitcheck.controller;

import katopia.fitcheck.controller.spec.DevApiSpec;
import katopia.fitcheck.dev.service.DevMemberService;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.MemberSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Profile("local")
public class DevController implements DevApiSpec {

    private final DevMemberService devMemberService;

    @DeleteMapping("/members/{memberId}")
    @Override
    public ResponseEntity<Void> hardDeleteMember(@PathVariable("memberId") Long memberId) {
        devMemberService.hardDeleteMember(memberId);
        return APIResponse.noContent(MemberSuccessCode.MEMBER_DELETED);
    }
}
