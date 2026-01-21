package katopia.fitcheck.global.security.oauth2;

import java.util.Locale;
import java.util.Map;

public final class SocialUserProfileFactory {

    private SocialUserProfileFactory() {} // 의존성 주입 제외 클래스 명시

    public static SocialUserProfile from(String registrationId, Map<String, Object> attributes) {
        SocialProvider provider = SocialProvider.valueOf(registrationId.toUpperCase(Locale.ROOT));
        return switch (provider) {
            default -> fromKakao(attributes);
        };
    }

    private static SocialUserProfile fromKakao(Map<String, Object> attributes) {
        Map<String, Object> account = getNested(attributes, "kakao_account");
        Map<String, Object> profile = (account != null ? getNested(account, "profile") : null);
        String email = (account != null ? (String) account.get("email") : null);
        String nickname = (profile != null ? (String) profile.get("nickname") : null);

        return new SocialUserProfile(
                SocialProvider.KAKAO,
                toStringSafe(attributes.get("id")),
                email,
                nickname
        );
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getNested(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    private static String toStringSafe(Object value) {
        if (value instanceof Number number) {
            return String.valueOf(number.longValue());
        }
        return value != null ? value.toString() : null;
    }
}
