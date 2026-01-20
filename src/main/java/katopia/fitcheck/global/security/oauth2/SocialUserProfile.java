package katopia.fitcheck.global.security.oauth2;

public record SocialUserProfile(
        SocialProvider provider,
        String providerUserId,
        String email,
        String nickname
) { }
