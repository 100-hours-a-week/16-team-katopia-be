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
import katopia.fitcheck.dto.vote.response.VoteSummary;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.repository.vote.VoteParticipationRepository;
import katopia.fitcheck.repository.vote.VoteRepository;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteParticipationRepository voteParticipationRepository;
    private final MemberFinder memberFinder;

    @Transactional
    public VoteCreateResponse create(Long memberId, VoteCreateRequest request) {
        Member member = memberFinder.findActiveByIdOrThrow(memberId);
        Vote vote = Vote.create(member, request);
        Vote saved = voteRepository.save(vote);
        return VoteCreateResponse.of(saved);
    }

    @Transactional(readOnly = true)
    public VoteListResponse listMine(Long memberId, String sizeValue, String after) {
        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Vote> votes = loadVotes(memberId, size, after);
        final LocalDateTime now = LocalDateTime.now();

        List<VoteSummary> summaries = votes.stream()
                .map(vote -> VoteSummary.of(vote, now))
                .toList();

        String nextCursor = null;
        if (!votes.isEmpty() && votes.size() == size) {
            Vote last = votes.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.getCreatedAt(), last.getId());
        }

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

    @Transactional
    public VoteResultResponse participate(Long memberId, Long voteId, VoteParticipationRequest request) {
        Vote vote = findVoteOrThrow(voteId);
        final LocalDateTime now = LocalDateTime.now();

        if (vote.getMember().getId().equals(memberId)) {
            throw new BusinessException(VoteErrorCode.SELF_PARTICIPATION_NOT_ALLOWED);
        }
        if (!vote.getExpiresAt().isAfter(now)) {
            throw new BusinessException(VoteErrorCode.VOTE_CLOSED);
        }
        if (voteParticipationRepository.existsByVoteIdAndMemberId(voteId, memberId)) {
            throw new BusinessException(VoteErrorCode.ALREADY_PARTICIPATED);
        }
        validateVoteItemIds(request.voteItemIds());
        List<VoteItem> selected = voteItemRepository.findByVoteIdAndIdIn(voteId, request.voteItemIds());
        if (selected.size() != request.voteItemIds().size()) {
            throw new BusinessException(VoteErrorCode.VOTE_ITEM_INVALID);
        }
        voteItemRepository.incrementFitCounts(voteId, request.voteItemIds());

        Member member = memberFinder.findActiveByIdOrThrow(memberId);
        voteParticipationRepository.save(VoteParticipation.of(vote, member, now));
        List<VoteItem> items = voteItemRepository.findByVoteIdOrderBySortOrder(voteId);
        return VoteResultResponse.of(vote, items);
    }

    @Transactional(readOnly = true)
    public VoteResultResponse getResult(Long memberId, Long voteId) {
        Vote vote = findVoteOrThrow(voteId);
        boolean isOwner = vote.getMember().getId().equals(memberId);
        boolean participated = voteParticipationRepository.existsByVoteIdAndMemberId(voteId, memberId);
        if (!isOwner && !participated) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }
        List<VoteItem> items = voteItemRepository.findByVoteIdOrderBySortOrder(voteId);
        return VoteResultResponse.of(vote, items);
    }

    @Transactional
    public void delete(Long memberId, Long voteId) {
        Vote vote = findVoteOrThrow(voteId);
        if (!vote.getMember().getId().equals(memberId)) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }
        voteParticipationRepository.deleteByVoteId(voteId);
        voteItemRepository.deleteByVoteId(voteId);
        voteRepository.delete(vote);
    }

    private List<Vote> loadVotes(Long memberId, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return voteRepository.findLatestByMemberId(memberId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return voteRepository.findPageAfter(memberId, cursor.createdAt(), cursor.id(), pageRequest);
    }

    private Vote findVoteOrThrow(Long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new BusinessException(VoteErrorCode.VOTE_NOT_FOUND));
    }

    private void validateVoteItemIds(List<Long> voteItemIds) {
        Set<Long> unique = new HashSet<>();
        for (Long id : voteItemIds) {
            if (!unique.add(id)) {
                throw new BusinessException(VoteErrorCode.VOTE_ITEM_DUPLICATED);
            }
        }
    }
}
