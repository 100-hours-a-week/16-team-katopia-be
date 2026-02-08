package katopia.fitcheck.domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Nested
    class RevokeCases {

        @Test
        @DisplayName("TC-REFRESH-TOKEN-01 revoke 호출 시 revokedAt 설정")
        void tcRefreshToken01_revoke_setsRevokedAt() {
            RefreshToken token = RefreshToken.issue(1L, "hash", LocalDateTime.now().plusDays(1));
            LocalDateTime now = LocalDateTime.now();

            token.revoke(now);

            assertThat(token.isRevoked()).isTrue();
            assertThat(token.getRevokedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("TC-REFRESH-TOKEN-02 revoke는 한번만 적용")
        void tcRefreshToken02_revoke_onlyOnce() {
            RefreshToken token = RefreshToken.issue(1L, "hash", LocalDateTime.now().plusDays(1));
            LocalDateTime first = LocalDateTime.now().minusMinutes(1);
            LocalDateTime second = LocalDateTime.now();

            token.revoke(first);
            token.revoke(second);

            assertThat(token.getRevokedAt()).isEqualTo(first);
        }
    }
}
