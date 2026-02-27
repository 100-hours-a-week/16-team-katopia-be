package katopia.fitcheck.service.vote;

import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.dto.vote.response.VoteCandidateResponse;
import katopia.fitcheck.dto.vote.response.VoteListResponse;
import katopia.fitcheck.dto.vote.response.VoteResultResponse;
import katopia.fitcheck.dto.vote.response.VoteSummary;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.repository.vote.VoteParticipationRepository;
import katopia.fitcheck.repository.vote.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteQueryService {

    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteParticipationRepository voteParticipationRepository;

    @Transactional(readOnly = true)
    public VoteListResponse listMine(Long memberId, String sizeValue, String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Vote> votes = loadVotes(memberId, size, after);
        final LocalDateTime now = LocalDateTime.now();

        List<VoteSummary> summaries = votes.stream()
                .map(vote -> VoteSummary.of(vote, now))
                .toList();

        String nextCursor = CursorPagingHelper.resolveNextCursor(votes, size, Vote::getCreatedAt, Vote::getId);
        return VoteListResponse.of(summaries, nextCursor);
    }

    @Transactional(readOnly = true)
    public VoteCandidateResponse findLatestCandidate(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        List<Vote> votes = voteRepository.findLatestCandidate(memberId, now, PageRequest.of(0, 1));
        if (votes.isEmpty()) {
            throw new BusinessException(VoteErrorCode.VOTE_NOT_FOUND);
        }
        Vote vote = votes.getFirst();
        List<VoteItem> items = voteItemRepository.findByVoteIdOrderBySortOrder(vote.getId());
        return VoteCandidateResponse.of(vote, items);
    }

    @Transactional(readOnly = true)
    public VoteResultResponse getResult(Long memberId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
            .orElseThrow(() -> new BusinessException(VoteErrorCode.VOTE_NOT_FOUND));
        boolean isOwner = vote.getMember().getId().equals(memberId);
        boolean participated = voteParticipationRepository.existsByVoteIdAndMemberId(voteId, memberId);
        if (!isOwner && !participated) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }
        List<VoteItem> items = voteItemRepository.findByVoteIdOrderBySortOrder(voteId);
        return VoteResultResponse.of(vote, items);
    }

    private List<Vote> loadVotes(Long memberId, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return voteRepository.findLatestByMemberId(memberId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return voteRepository.findPageAfter(memberId, cursor.createdAt(), cursor.id(), pageRequest);
    }
}
