package katopia.fitcheck.service.auth;

import katopia.fitcheck.global.policy.Policy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

final class RefreshTokenHashSupport {

    private RefreshTokenHashSupport() {
    }

    static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance(Policy.REFRESH_TOKEN_HASH_ALGORITHM);
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to hash refresh token", ex);
        }
    }
}
