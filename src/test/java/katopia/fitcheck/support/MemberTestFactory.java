package katopia.fitcheck.support;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.security.oauth2.SocialProvider;

public final class MemberTestFactory {

    private MemberTestFactory() {
    }

    public static Member member(Long id) {
        return builder(id, "member-" + id).build();
    }

    public static Member member(Long id, String nickname) {
        return builder(id, nickname).build();
    }

    public static Member.MemberBuilder builder(Long id, String nickname) {
        return Member.builder()
                .id(id)
                .nickname(nickname)
                .oauth2Provider(SocialProvider.KAKAO)
                .oauth2UserId("oauth-" + id)
                .enableRealtimeNotification(false);
    }
}
