package katopia.fitcheck.global.security.oauth2;

import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Member member;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            Member member
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.member = member;
    }

    public boolean registrationRequired() {
        return member.getAccountStatus() == AccountStatus.PENDING;
    }
}
