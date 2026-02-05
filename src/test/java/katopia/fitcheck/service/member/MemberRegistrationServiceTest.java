package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Gender;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.StyleType;
import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.service.auth.RefreshTokenService;
import katopia.fitcheck.service.member.MemberProfileInputResolver.ResolvedProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberRegistrationServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private MemberProfileInputResolver profileInputResolver;

    @InjectMocks
    private MemberRegistrationService service;

    @Test
    @DisplayName("TC-AUTH-05 회원가입 완료 시 등록 쿠키 만료")
    void tcAuth05_signupClearsRegistrationCookie() {
        Member member = pendingMember();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        JwtProvider.Token accessToken = new JwtProvider.Token("at", Instant.now().plusSeconds(60));
        JwtProvider.Token refreshToken = new JwtProvider.Token("rt", Instant.now().plusSeconds(120));
        when(jwtProvider.issueTokens(1L)).thenReturn(new JwtProvider.TokenPair(accessToken, refreshToken));
        when(jwtProvider.buildRefreshCookie(refreshToken))
                .thenReturn(ResponseCookie.from(JwtProvider.REFRESH_COOKIE, "rt").maxAge(120).build());
        when(jwtProvider.clearRegistrationCookie())
                .thenReturn(ResponseCookie.from(JwtProvider.REGISTRATION_COOKIE, "").maxAge(0).build());

        MemberSignupRequest request = minimalSignupRequest();
        when(profileInputResolver.resolveForSignup(eq(member), eq(request)))
                .thenReturn(new ResolvedProfile(
                        "newnick",
                        null,
                        Gender.M,
                        null,
                        null,
                        false,
                        null
                ));

        MemberRegistrationService.SignupResult result = service.signup(1L, request);

        assertThat(result.clearRegistrationCookie().getMaxAge()).isZero();
    }

    @Test
    @DisplayName("TC-MEMBER-REG-01 회원가입 실패(임시 토큰 불일치)")
    void tcMemberReg01_invalidTempToken() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.signup(1L, minimalSignupRequest()))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.INVALID_TEMP_TOKEN);
    }

    @Test
    @DisplayName("TC-MEMBER-REG-02 회원가입 실패(이미 가입 완료)")
    void tcMemberReg02_alreadyRegistered() {
        Member member = pendingMember();
        ReflectionTestUtils.setField(member, "accountStatus", AccountStatus.ACTIVE);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.signup(1L, minimalSignupRequest()))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ALREADY_REGISTERED);
    }

    @Test
    @DisplayName("TC-MEMBER-REG-03 회원가입 실패(탈퇴 유예 기간)")
    void tcMemberReg03_withdrawnWithinGracePeriod() {
        Member member = pendingMember();
        ReflectionTestUtils.setField(member, "accountStatus", AccountStatus.WITHDRAWN);
        ReflectionTestUtils.setField(member, "deletedAt", LocalDateTime.now().minusDays(1));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.signup(1L, minimalSignupRequest()))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.WITHDRAWN_MEMBER);
    }

    @Test
    @DisplayName("TC-MEMBER-REG-04 회원가입 실패(닉네임 중복)")
    void tcMemberReg04_duplicateNickname() {
        Member member = pendingMember();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(memberRepository.existsByNickname("newnick")).thenReturn(true);

        assertThatThrownBy(() -> service.signup(1L, minimalSignupRequest()))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("TC-MEMBER-REG-05 회원가입 실패(DB 유니크 충돌)")
    void tcMemberReg05_duplicateNicknameOnFlush() {
        Member member = pendingMember();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(memberRepository.existsByNickname("newnick")).thenReturn(false);
        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("dup"))
                .when(memberRepository)
                .flush();
        MemberSignupRequest request = minimalSignupRequest();
        when(profileInputResolver.resolveForSignup(eq(member), eq(request)))
                .thenReturn(new ResolvedProfile(
                        "newnick",
                        null,
                        Gender.M,
                        null,
                        null,
                        false,
                        null
                ));

        assertThatThrownBy(() -> service.signup(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("TC-MEMBER-REG-06 회원가입 성공(선택 필드 입력)")
    void tcMemberReg06_signupWithOptionalFields() {
        Member member = pendingMember();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(memberRepository.existsByNickname("newnick")).thenReturn(false);

        JwtProvider.Token accessToken = new JwtProvider.Token("at", Instant.now().plusSeconds(60));
        JwtProvider.Token refreshToken = new JwtProvider.Token("rt", Instant.now().plusSeconds(120));
        when(jwtProvider.issueTokens(1L)).thenReturn(new JwtProvider.TokenPair(accessToken, refreshToken));
        when(jwtProvider.buildRefreshCookie(refreshToken))
                .thenReturn(ResponseCookie.from(JwtProvider.REFRESH_COOKIE, "rt").maxAge(120).build());
        when(jwtProvider.clearRegistrationCookie())
                .thenReturn(ResponseCookie.from(JwtProvider.REGISTRATION_COOKIE, "").maxAge(0).build());

        MemberSignupRequest request = new MemberSignupRequest(
                "newnick",
                "M",
                "profiles/1/1700000000000-uuid.png",
                "175",
                "70",
                true,
                java.util.List.of("CASUAL", "MINIMAL")
        );
        when(profileInputResolver.resolveForSignup(eq(member), eq(request)))
                .thenReturn(new ResolvedProfile(
                        "newnick",
                        "profiles/1/1700000000000-uuid.png",
                        Gender.M,
                        (short) 175,
                        (short) 70,
                        true,
                        Set.of(StyleType.CASUAL, StyleType.MINIMAL)
                ));

        MemberRegistrationService.SignupResult result = service.signup(1L, request);

        assertThat(result.accessToken()).isEqualTo("at");
        assertThat(member.getProfileImageObjectKey()).isEqualTo("profiles/1/1700000000000-uuid.png");
        assertThat(member.getHeight()).isEqualTo((short) 175);
        assertThat(member.getWeight()).isEqualTo((short) 70);
        assertThat(member.isEnableRealtimeNotification()).isTrue();
        assertThat(member.getStyles()).containsExactlyInAnyOrder(StyleType.CASUAL, StyleType.MINIMAL);
    }

    private MemberSignupRequest minimalSignupRequest() {
        return new MemberSignupRequest(
                "newnick",
                "M",
                null,
                null,
                null,
                null,
                null
        );
    }

    private Member pendingMember() {
        return Member.builder()
                .id(1L)
                .nickname("pending")
                .oauth2Provider(SocialProvider.KAKAO)
                .oauth2UserId("1")
                .accountStatus(AccountStatus.PENDING)
                .build();
    }
}
