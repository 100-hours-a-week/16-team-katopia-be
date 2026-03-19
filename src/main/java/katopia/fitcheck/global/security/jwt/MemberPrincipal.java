package katopia.fitcheck.global.security.jwt;

import java.security.Principal;

public record MemberPrincipal(Long memberId) implements Principal {

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
