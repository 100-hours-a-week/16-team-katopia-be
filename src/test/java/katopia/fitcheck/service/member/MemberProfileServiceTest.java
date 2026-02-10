package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.Gender;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.StyleType;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberFinder memberFinder;

    @Mock
    private MemberProfileInputResolver profileInputResolver;

    @InjectMocks
    private MemberProfileService memberProfileService;

    @Test
    @DisplayName("TC-MEMBER-PROFILE-04 프로필 수정 성공(선택값 누락)")
    void tcMemberProfile04_updateWithOptionalNulls_keepsExisting() {
        Member member = MemberTestFactory.builder(1L, "nickname")
                .profileImageObjectKey("profiles/1/old.png")
                .gender(Gender.M)
                .height((short) 175)
                .weight((short) 70)
                .enableRealtimeNotification(true)
                .styles(Set.of(StyleType.CASUAL))
                .build();
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(memberFinder.findByIdOrThrow(eq(1L))).thenReturn(member);
        when(profileInputResolver.resolveForUpdate(eq(member), eq(request)))
                .thenReturn(new MemberProfileInputResolver.ResolvedProfile(
                        member.getNickname(),
                        member.getProfileImageObjectKey(),
                        member.getGender(),
                        member.getHeight(),
                        member.getWeight(),
                        member.isEnableRealtimeNotification(),
                        Set.copyOf(member.getStyles())
                ));

        MemberProfileDetailResponse response = memberProfileService.updateProfile(1L, request);

        assertThat(member.getNickname()).isEqualTo("nickname");
        assertThat(member.getProfileImageObjectKey()).isEqualTo("profiles/1/old.png");
        assertThat(member.getGender()).isEqualTo(Gender.M);
        assertThat(member.getHeight()).isEqualTo((short) 175);
        assertThat(member.getWeight()).isEqualTo((short) 70);
        assertThat(member.isEnableRealtimeNotification()).isTrue();
        assertThat(member.getStyles()).containsExactly(StyleType.CASUAL);
        assertThat(response.profile().nickname()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("TC-MEMBER-PROFILE-01 프로필 수정 실패(탈퇴 회원)")
    void tcMemberProfile01_updateWithdrawnMember_throws() {
        Member member = MemberTestFactory.builder(1L, "nickname")
                .accountStatus(katopia.fitcheck.domain.member.AccountStatus.WITHDRAWN)
                .build();
        when(memberFinder.findByIdOrThrow(eq(1L))).thenReturn(member);

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "newnick",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> memberProfileService.updateProfile(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.NOT_FOUND_WITHDRAWN_MEMBER);
        verify(profileInputResolver, never()).resolveForUpdate(eq(member), eq(request));
    }

    @Test
    @DisplayName("TC-MEMBER-PROFILE-02 프로필 수정 실패(닉네임 중복)")
    void tcMemberProfile02_updateDuplicateNickname_throws() {
        Member member = MemberTestFactory.member(1L);
        when(memberFinder.findByIdOrThrow(eq(1L))).thenReturn(member);

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "newnick",
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(profileInputResolver.resolveForUpdate(eq(member), eq(request)))
                .thenReturn(new MemberProfileInputResolver.ResolvedProfile(
                        "newnick",
                        member.getProfileImageObjectKey(),
                        member.getGender(),
                        member.getHeight(),
                        member.getWeight(),
                        member.isEnableRealtimeNotification(),
                        member.getStyles()
                ));
        when(memberRepository.existsByNickname("newnick")).thenReturn(true);

        assertThatThrownBy(() -> memberProfileService.updateProfile(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("TC-MEMBER-PROFILE-03 프로필 수정 실패(성별/키/몸무게 파싱 오류)")
    void tcMemberProfile03_updateInvalidProfile_throws() {
        Member member = MemberTestFactory.member(1L);
        when(memberFinder.findByIdOrThrow(eq(1L))).thenReturn(member);

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                null,
                null,
                "X",
                "abc",
                "999",
                null,
                null
        );
        when(profileInputResolver.resolveForUpdate(eq(member), eq(request)))
                .thenThrow(new BusinessException(MemberErrorCode.INVALID_GENDER_FORMAT));

        assertThatThrownBy(() -> memberProfileService.updateProfile(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.INVALID_GENDER_FORMAT);
    }

    @Test
    @DisplayName("TC-MEMBER-WITHDRAW-01 회원 탈퇴 실패(이미 탈퇴)")
    void tcMemberWithdraw01_alreadyWithdrawn_throws() {
        Member member = MemberTestFactory.builder(1L, "nickname")
                .accountStatus(katopia.fitcheck.domain.member.AccountStatus.WITHDRAWN)
                .build();
        when(memberFinder.findByIdOrThrow(eq(1L))).thenReturn(member);

        assertThatThrownBy(() -> memberProfileService.withdraw(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.NOT_FOUND_WITHDRAWN_MEMBER);
    }
}
