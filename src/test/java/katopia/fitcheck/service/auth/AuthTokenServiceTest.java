package katopia.fitcheck.service.auth;

import katopia.fitcheck.domain.auth.RefreshToken;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthTokenService authTokenService;

    @Test
    @DisplayName("TC-AUTH-SVC-F-01 토큰 재발급 실패(토큰 파싱 실패)")
    void tcAuthSvcF01_invalidToken() {
        when(jwtProvider.extractMemberId(eq("rt"), eq(JwtProvider.TokenType.REFRESH))).thenReturn(null);

        assertThatThrownBy(() -> authTokenService.refreshTokens("rt"))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.INVALID_RT);
    }

    @Test
    @DisplayName("TC-AUTH-SVC-F-02 토큰 재발급 실패(미등록 토큰)")
    void tcAuthSvcF02_missingTokenEntity() {
        when(jwtProvider.extractMemberId(eq("rt"), eq(JwtProvider.TokenType.REFRESH))).thenReturn(1L);
        String tokenHash = RefreshTokenHashSupport.hash("rt");
        when(refreshTokenService.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authTokenService.refreshTokens("rt"))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.INVALID_RT);
    }

    @Test
    @DisplayName("TC-AUTH-SVC-F-03 토큰 재발급 실패(폐기/만료)")
    void tcAuthSvcF03_revokedOrExpiredToken() {
        when(jwtProvider.extractMemberId(eq("rt"), eq(JwtProvider.TokenType.REFRESH))).thenReturn(1L);
        String tokenHash = RefreshTokenHashSupport.hash("rt");
        RefreshToken entity = RefreshToken.issue(1L, tokenHash, LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(entity, "revokedAt", LocalDateTime.now());
        when(refreshTokenService.findByTokenHash(tokenHash)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> authTokenService.refreshTokens("rt"))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.INVALID_RT);
        verify(refreshTokenService).revokeAllByMemberId(eq(1L), any());
        verify(memberRepository, never()).existsByIdAndAccountStatus(any(), any());
    }

    @Test
    @DisplayName("TC-AUTH-SVC-F-04 토큰 재발급 실패(탈퇴 회원)")
    void tcAuthSvcF04_withdrawnMember() {
        when(jwtProvider.extractMemberId(eq("rt"), eq(JwtProvider.TokenType.REFRESH))).thenReturn(1L);
        String tokenHash = RefreshTokenHashSupport.hash("rt");
        RefreshToken entity = RefreshToken.issue(1L, tokenHash, LocalDateTime.now().plusDays(1));
        when(refreshTokenService.findByTokenHash(tokenHash)).thenReturn(Optional.of(entity));
        when(memberRepository.existsByIdAndAccountStatus(1L, katopia.fitcheck.domain.member.AccountStatus.WITHDRAWN))
                .thenReturn(true);

        assertThatThrownBy(() -> authTokenService.refreshTokens("rt"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER);
    }

}
