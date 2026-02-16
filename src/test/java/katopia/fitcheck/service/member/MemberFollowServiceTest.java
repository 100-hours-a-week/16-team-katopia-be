package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.MemberFollow;
import katopia.fitcheck.dto.member.response.MemberFollowListResponse;
import katopia.fitcheck.dto.member.response.MemberFollowSummary;
import katopia.fitcheck.dto.member.response.MemberFollowResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.service.notification.NotificationService;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberFollowServiceTest {

    @Mock
    private MemberFollowRepository memberFollowRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberFinder memberFinder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MemberFollowService memberFollowService;

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-S-01 팔로우 성공(응답 집계 포함)")
    void tcMemberFollowS01_followSuccess_returnsAggregate() {
        Member follower = activeMember(1L, "follower");
        Member followed = MemberTestFactory.builder(2L, "target")
                .accountStatus(AccountStatus.ACTIVE)
                .followerCount(2L)
                .followingCount(3L)
                .build();
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(follower);
        when(memberFinder.findActiveByIdOrThrow(2L)).thenReturn(followed);
        Member updatedTarget = MemberTestFactory.builder(2L, "target")
                .accountStatus(AccountStatus.ACTIVE)
                .followerCount(3L)
                .followingCount(3L)
                .build();
        when(memberFinder.findByIdOrThrow(2L)).thenReturn(updatedTarget);
        when(memberFollowRepository.existsByFollowerIdAndFollowedId(1L, 2L)).thenReturn(false);
        when(memberFollowRepository.save(any(MemberFollow.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(memberRepository.incrementFollowingCount(1L)).thenReturn(1);
        when(memberRepository.incrementFollowerCount(2L)).thenReturn(1);

        MemberFollowResponse response = memberFollowService.follow(1L, 2L);

        verify(notificationService).createFollow(eq(1L), eq(2L));
        assertThat(response.isFollowing()).isTrue();
        assertThat(response.targetId()).isEqualTo(2L);
        assertThat(response.targetNickname()).isEqualTo("target");
        assertThat(response.aggregate().followerCount()).isEqualTo(3L);
        assertThat(response.aggregate().followingCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-S-02 언팔로우 성공(응답 집계 포함)")
    void tcMemberFollowS02_unfollowSuccess_returnsAggregate() {
        Member follower = activeMember(1L, "follower");
        Member followed = MemberTestFactory.builder(2L, "target")
                .accountStatus(AccountStatus.ACTIVE)
                .followerCount(5L)
                .followingCount(1L)
                .build();
        MemberFollow follow = MemberFollow.of(follower, followed);
        when(memberFollowRepository.findByFollowerIdAndFollowedId(1L, 2L)).thenReturn(Optional.of(follow));
        Member updatedTarget = MemberTestFactory.builder(2L, "target")
                .accountStatus(AccountStatus.ACTIVE)
                .followerCount(4L)
                .followingCount(1L)
                .build();
        when(memberFinder.findByIdOrThrow(2L)).thenReturn(updatedTarget);
        when(memberRepository.decrementFollowingCount(1L)).thenReturn(1);
        when(memberRepository.decrementFollowerCount(2L)).thenReturn(1);

        MemberFollowResponse response = memberFollowService.unfollow(1L, 2L);

        assertThat(response.isFollowing()).isFalse();
        assertThat(response.targetId()).isEqualTo(2L);
        assertThat(response.targetNickname()).isEqualTo("target");
        assertThat(response.aggregate().followerCount()).isEqualTo(4L);
        assertThat(response.aggregate().followingCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-S-03 팔로워 목록 조회 성공(커서 포함)")
    void tcMemberFollowS03_listFollowers_returnsCursor() {
        Member member = activeMember(2L, "target");
        when(memberFinder.findPublicProfileByIdOrThrow(2L)).thenReturn(member);

        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 9, 12, 0);
        List<MemberFollowSummary> projections = List.of(
                new MemberFollowSummary(11L, createdAt, 1L, "follower", "profiles/1.png")
        );
        when(memberFollowRepository.findFollowersLatest(eq(2L), any())).thenReturn(projections);

        MemberFollowListResponse response = memberFollowService.listFollowers(2L, "1", null);

        assertThat(response.members()).hasSize(1);
        assertThat(response.members().getFirst().id()).isEqualTo(1L);
        assertThat(response.nextCursor()).isEqualTo(CursorPagingHelper.encodeCursor(createdAt, 11L));
    }

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-S-04 팔로잉 목록 조회 성공(커서 포함)")
    void tcMemberFollowS04_listFollowings_returnsCursor() {
        Member member = activeMember(2L, "target");
        when(memberFinder.findPublicProfileByIdOrThrow(2L)).thenReturn(member);

        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 9, 13, 0);
        List<MemberFollowSummary> projections = List.of(
                new MemberFollowSummary(12L, createdAt, 3L, "followed", "profiles/3.png")
        );
        when(memberFollowRepository.findFollowingsLatest(eq(2L), any())).thenReturn(projections);

        MemberFollowListResponse response = memberFollowService.listFollowings(2L, "1", null);

        assertThat(response.members()).hasSize(1);
        assertThat(response.members().getFirst().id()).isEqualTo(3L);
        assertThat(response.nextCursor()).isEqualTo(CursorPagingHelper.encodeCursor(createdAt, 12L));
    }

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-F-01 팔로우 실패(자기 자신)")
    void tcMemberFollowF01_selfFollow_throws() {
        assertThatThrownBy(() -> memberFollowService.follow(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.SELF_FOLLOW_NOT_ALLOWED);
    }

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-F-02 팔로우 실패(중복 팔로우)")
    void tcMemberFollowF02_alreadyFollowing_throws() {
        Member follower = activeMember(1L, "follower");
        Member followed = activeMember(2L, "followed");
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(follower);
        when(memberFinder.findActiveByIdOrThrow(2L)).thenReturn(followed);
        when(memberFollowRepository.existsByFollowerIdAndFollowedId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> memberFollowService.follow(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.ALREADY_FOLLOWING);
    }

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-F-03 언팔로우 실패(자기 자신)")
    void tcMemberFollowF03_selfUnfollow_throws() {
        assertThatThrownBy(() -> memberFollowService.unfollow(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.SELF_FOLLOW_NOT_ALLOWED);
    }

    @Test
    @DisplayName("TC-MEMBER-FOLLOW-F-04 언팔로우 실패(중복 언팔로우/관계 없음)")
    void tcMemberFollowF04_notFollowing_throws() {
        when(memberFollowRepository.findByFollowerIdAndFollowedId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberFollowService.unfollow(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(MemberErrorCode.NOT_FOLLOWING);
    }

    private Member activeMember(Long id, String nickname) {
        return MemberTestFactory.builder(id, nickname)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

}
