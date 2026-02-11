package katopia.fitcheck.service.vote;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.domain.vote.VoteParticipation;
import katopia.fitcheck.dto.vote.request.VoteCreateRequest;
import katopia.fitcheck.dto.vote.request.VoteParticipationRequest;
import katopia.fitcheck.dto.vote.response.VoteCandidateResponse;
import katopia.fitcheck.dto.vote.response.VoteCreateResponse;
import katopia.fitcheck.dto.vote.response.VoteListResponse;
import katopia.fitcheck.dto.vote.response.VoteResultResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.repository.vote.VoteParticipationRepository;
import katopia.fitcheck.repository.vote.VoteRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.support.MemberTestFactory;
import katopia.fitcheck.domain.vote.VoteTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VoteItemRepository voteItemRepository;

    @Mock
    private VoteParticipationRepository voteParticipationRepository;

    @Mock
    private MemberFinder memberFinder;

    @InjectMocks
    private VoteService voteService;

    @Test
    @DisplayName("TC-VOTE-S-01 투표 생성: 이미지 키 반환")
    void create_returnsImageKeys() {
        Member member = MemberTestFactory.member(1L);
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(member);

        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> {
            Vote vote = invocation.getArgument(0);
            ReflectionTestUtils.setField(vote, "id", 10L);
            return vote;
        });

        VoteCreateRequest request = new VoteCreateRequest(
                "title",
                List.of("votes/1/a.png", "votes/1/b.png")
        );

        VoteCreateResponse response = voteService.create(1L, request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("title");
        assertThat(response.imageObjectKeys()).containsExactly("votes/1/a.png", "votes/1/b.png");
    }

    @Test
    @DisplayName("TC-VOTE-S-02 내 투표 목록: 커서 생성")
    void listMine_buildsCursor() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 11, 10, 0, 0);
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(1L), "title", createdAt.plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 5L);
        ReflectionTestUtils.setField(vote, "createdAt", createdAt);
        when(voteRepository.findLatestByMemberId(eq(1L), eq(PageRequest.of(0, 1))))
                .thenReturn(List.of(vote));

        VoteListResponse response = voteService.listMine(1L, "1", null);

        assertThat(response.votes()).hasSize(1);
        assertThat(response.nextCursor()).isEqualTo("2026-02-11T10:00:00|5");
    }

    @Test
    @DisplayName("TC-VOTE-S-03 참여 가능한 최신 투표 조회")
    void findLatestCandidate_returnsLatest() {
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(2L), "title", LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 10L);
        when(voteRepository.findLatestCandidate(eq(1L), any(), eq(PageRequest.of(0, 1))))
                .thenReturn(List.of(vote));

        VoteItem item = VoteItem.of(vote, 1, "votes/1/a.png");
        ReflectionTestUtils.setField(item, "id", 100L);
        when(voteItemRepository.findByVoteIdOrderBySortOrder(10L)).thenReturn(List.of(item));

        VoteCandidateResponse response = voteService.findLatestCandidate(1L);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().id()).isEqualTo(100L);
    }

    @Test
    @DisplayName("TC-VOTE-S-04 투표 참여: 결과 반환")
    void participate_returnsResult() {
        Member member = MemberTestFactory.member(1L);
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(2L), "title", LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 10L);
        when(voteRepository.findById(10L)).thenReturn(java.util.Optional.of(vote));
        when(voteParticipationRepository.existsByVoteIdAndMemberId(10L, 1L)).thenReturn(false);
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(member);

        VoteItem item1 = VoteItem.of(vote, 1, "votes/1/a.png");
        VoteItem item2 = VoteItem.of(vote, 2, "votes/1/b.png");
        ReflectionTestUtils.setField(item1, "id", 100L);
        ReflectionTestUtils.setField(item2, "id", 101L);
        when(voteItemRepository.findByVoteIdAndIdIn(10L, List.of(100L, 101L)))
                .thenReturn(List.of(item1, item2));
        when(voteItemRepository.incrementFitCounts(10L, List.of(100L, 101L))).thenReturn(2);

        ReflectionTestUtils.setField(item1, "fitCount", 2L);
        ReflectionTestUtils.setField(item2, "fitCount", 1L);
        when(voteItemRepository.findByVoteIdOrderBySortOrder(10L)).thenReturn(List.of(item1, item2));

        VoteParticipationRequest request = new VoteParticipationRequest(List.of(100L, 101L));
        VoteResultResponse response = voteService.participate(1L, 10L, request);

        assertThat(response.items()).hasSize(2);
        assertThat(response.items().getFirst().fitRate()).isEqualByComparingTo(BigDecimal.valueOf(66.67));
        verify(voteParticipationRepository).save(any(VoteParticipation.class));
    }

    @Test
    @DisplayName("TC-VOTE-F-01 투표 참여 실패: 중복 투표 항목")
    void participate_failsWhenDuplicateItems() {
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(2L), "title", LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 10L);
        when(voteRepository.findById(10L)).thenReturn(java.util.Optional.of(vote));
        when(voteParticipationRepository.existsByVoteIdAndMemberId(10L, 1L)).thenReturn(false);

        VoteParticipationRequest request = new VoteParticipationRequest(List.of(100L, 100L));

        assertThatThrownBy(() -> voteService.participate(1L, 10L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(VoteErrorCode.VOTE_ITEM_DUPLICATED);
    }

    @Test
    @DisplayName("TC-VOTE-F-02 투표 결과 조회 실패: 참여/작성자 아님")
    void getResult_failsWhenNotParticipant() {
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(2L), "title", LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 10L);
        when(voteRepository.findById(10L)).thenReturn(java.util.Optional.of(vote));
        when(voteParticipationRepository.existsByVoteIdAndMemberId(10L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> voteService.getResult(1L, 10L))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("TC-VOTE-F-03 투표 삭제 실패: 작성자 아님")
    void delete_requiresOwner() {
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(2L), "title", LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 10L);
        when(voteRepository.findById(10L)).thenReturn(java.util.Optional.of(vote));

        assertThatThrownBy(() -> voteService.delete(1L, 10L))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }
}
