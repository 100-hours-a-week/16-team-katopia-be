package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberFinderTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberFinder memberFinder;

    @Test
    @DisplayName("TC-MEMBER-FINDER-S-01 활성 회원 조회 성공")
    void tcMemberFinderS01_findActiveById_returnsMember() {
        Member member = MemberTestFactory.builder(1L, "member")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        when(memberRepository.findByIdAndAccountStatus(eq(1L), eq(AccountStatus.ACTIVE)))
                .thenReturn(Optional.of(member));

        Member result = memberFinder.findActiveByIdOrThrow(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("TC-MEMBER-FINDER-F-01 활성 회원 조회 실패(미존재/비활성)")
    void tcMemberFinderF01_findActiveById_throws() {
        when(memberRepository.findByIdAndAccountStatus(eq(1L), eq(AccountStatus.ACTIVE)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberFinder.findActiveByIdOrThrow(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER);
    }
}
