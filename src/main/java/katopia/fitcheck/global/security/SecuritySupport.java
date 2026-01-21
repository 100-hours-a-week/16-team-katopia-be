package katopia.fitcheck.global.security;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.stereotype.Component;

@Component
public class SecuritySupport {

    public Long requireMemberId(MemberPrincipal principal) {
        if (principal == null) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_AT);
        }
        return principal.memberId();
    }
}
