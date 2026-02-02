package katopia.fitcheck.global.security.jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRegistrationController {

    @GetMapping("/api/members/check")
    public String check() {
        return "ok";
    }

    @PostMapping("/api/members")
    public String signup() {
        return "ok";
    }
}
