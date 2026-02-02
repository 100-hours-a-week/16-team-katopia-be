package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.MemberProfileValidator;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.service.auth.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemberRegistrationServiceTest {

    @Test
    @DisplayName("TC-AUTH-05 회원가입 완료 시 등록 쿠키 만료")
    void tcAuth05_signupClearsRegistrationCookie() {
        MemberRepository memberRepository = mock(MemberRepository.class);
        JwtProvider jwtProvider = mock(JwtProvider.class);
        MemberProfileValidator profileValidator = mock(MemberProfileValidator.class);
        RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
        MemberRegistrationService service = new MemberRegistrationService(memberRepository, jwtProvider, profileValidator, refreshTokenService);

        Member member = Member.builder()
                .id(1L)
                .nickname("pending")
                .oauth2Provider(katopia.fitcheck.global.security.oauth2.SocialProvider.KAKAO)
                .oauth2UserId("1")
                .accountStatus(AccountStatus.PENDING)
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(profileValidator.parseGender("M")).thenReturn(katopia.fitcheck.domain.member.Gender.M);

        JwtProvider.Token accessToken = new JwtProvider.Token("at", Instant.now().plusSeconds(60));
        JwtProvider.Token refreshToken = new JwtProvider.Token("rt", Instant.now().plusSeconds(120));
        when(jwtProvider.issueTokens(1L)).thenReturn(new JwtProvider.TokenPair(accessToken, refreshToken));
        when(jwtProvider.buildRefreshCookie(refreshToken))
                .thenReturn(ResponseCookie.from(JwtProvider.REFRESH_COOKIE, "rt").maxAge(120).build());
        when(jwtProvider.clearRegistrationCookie())
                .thenReturn(ResponseCookie.from(JwtProvider.REGISTRATION_COOKIE, "").maxAge(0).build());

        MemberRegistrationService.SignupResult result = service.signup(1L, "newnick", "M");

        assertThat(result.clearRegistrationCookie().getMaxAge()).isZero();
    }
}
