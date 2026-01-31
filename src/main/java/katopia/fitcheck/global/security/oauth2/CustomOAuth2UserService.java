package katopia.fitcheck.global.security.oauth2;

import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberFinder memberFinder;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        SocialUserProfile profile = SocialUserProfileFactory.from(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );

        Member member = memberFinder.findBySocialProfileOrNull(profile);

        // 최초 소셜로그인 사용자 처리
        if (member == null) {
            member = Member.createPending(
                profile.provider(),
                profile.providerUserId(),
                profile.email(),
                resolveNicknameWithFallback(profile.nickname())
            );
            memberRepository.save(member);
        }

        String nameAttributeKey = oAuth2UserRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new CustomOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")), // 관리자 계정은 현재 필요하지 않으므로 "ROLE_USER" 고정
                oAuth2User.getAttributes(),
                nameAttributeKey,
                member
        );
    }

    private String resolveNicknameWithFallback(String nickname) {
        // 카카오 닉네임 최대 길이는 200자
        return nickname.length() <= 20 ? nickname : UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20);
    }
}
