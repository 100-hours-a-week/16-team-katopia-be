package katopia.fitcheck.global.security;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecuritySupportTest {

    private final SecuritySupport securitySupport = new SecuritySupport();

    @Test
    @DisplayName("TC-SEC-SUPPORT-01 인증 principal이 없으면 예외")
    void tcSecSupport01_requireMemberId_throwsWhenNull() {
        assertThatThrownBy(() -> securitySupport.requireMemberId(null))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.NOT_FOUND_AT);
    }

    @Test
    @DisplayName("TC-SEC-SUPPORT-02 인증 principal이 있으면 memberId 반환")
    void tcSecSupport02_requireMemberId_returnsId() {
        Long memberId = securitySupport.requireMemberId(new MemberPrincipal(1L));

        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    @DisplayName("TC-SEC-SUPPORT-03 null principal이면 null 반환")
    void tcSecSupport03_findMemberIdOrNull_returnsNull() {
        assertThat(securitySupport.findMemberIdOrNull(null)).isNull();
    }

    @Test
    @DisplayName("TC-SEC-SUPPORT-04 principal이 있으면 memberId 반환")
    void tcSecSupport04_findMemberIdOrNull_returnsId() {
        assertThat(securitySupport.findMemberIdOrNull(new MemberPrincipal(2L))).isEqualTo(2L);
    }
}
