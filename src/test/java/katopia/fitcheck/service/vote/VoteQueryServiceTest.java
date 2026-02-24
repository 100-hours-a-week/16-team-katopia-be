package katopia.fitcheck.service.vote;

import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.domain.vote.VoteTestFactory;
import katopia.fitcheck.dto.vote.response.VoteCandidateResponse;
import katopia.fitcheck.dto.vote.response.VoteListResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.repository.vote.VoteParticipationRepository;
import katopia.fitcheck.repository.vote.VoteRepository;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteQueryServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VoteItemRepository voteItemRepository;

    @Mock
    private VoteParticipationRepository voteParticipationRepository;

    @InjectMocks
    private VoteQueryService voteQueryService;

    @Test
    @DisplayName("TC-VOTE-S-02 내 투표 목록: 커서 생성")
    void listMine_buildsCursor() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 11, 10, 0, 0);
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(1L), "title", createdAt.plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 5L);
        ReflectionTestUtils.setField(vote, "createdAt", createdAt);
        when(voteRepository.findLatestByMemberId(eq(1L), eq(PageRequest.of(0, 1))))
                .thenReturn(List.of(vote));

        VoteListResponse response = voteQueryService.listMine(1L, "1", null);

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

        VoteCandidateResponse response = voteQueryService.findLatestCandidate(1L);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().id()).isEqualTo(100L);
    }

    @Test
    @DisplayName("TC-VOTE-F-02 투표 결과 조회 실패: 참여/작성자 아님")
    void getResult_failsWhenNotParticipant() {
        Vote vote = VoteTestFactory.vote(MemberTestFactory.member(2L), "title", LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(vote, "id", 10L);
        when(voteRepository.findById(10L)).thenReturn(java.util.Optional.of(vote));
        when(voteParticipationRepository.existsByVoteIdAndMemberId(10L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> voteQueryService.getResult(1L, 10L))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.ACCESS_DENIED);
    }
}
