package katopia.fitcheck.member;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.global.security.oauth2.SocialUserProfile;
import katopia.fitcheck.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFinder {
    private final MemberRepository memberRepository;

    @Transactional
    public Member findBySocialProfileOrNull(SocialUserProfile profile) {
        return memberRepository.findByOauth2ProviderAndOauth2UserId(profile.provider(), profile.providerUserId())
                .orElse(null);
    }
}
