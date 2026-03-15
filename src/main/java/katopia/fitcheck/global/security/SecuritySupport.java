package katopia.fitcheck.global.security;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class SecuritySupport {

    public Long requireMemberId(MemberPrincipal principal) {
        if (principal == null) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_AT);
        }
        return principal.memberId();
    }

    public Long requireMemberId(Principal principal) {
        if (principal == null) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_AT);
        }
        if (principal instanceof MemberPrincipal memberPrincipal) {
            return memberPrincipal.memberId();
        }
        throw new AuthException(AuthErrorCode.INVALID_AT);
    }

    public Long findMemberIdOrNull(MemberPrincipal principal) {
        return principal != null ? principal.memberId() : null;
    }
}
