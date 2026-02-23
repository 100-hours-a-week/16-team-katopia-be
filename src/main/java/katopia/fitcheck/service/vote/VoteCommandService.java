package katopia.fitcheck.service.vote;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.domain.vote.VoteParticipation;
import katopia.fitcheck.dto.vote.request.VoteCreateRequest;
import katopia.fitcheck.dto.vote.request.VoteParticipationRequest;
import katopia.fitcheck.dto.vote.response.VoteCreateResponse;
import katopia.fitcheck.dto.vote.response.VoteResultResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.repository.vote.VoteParticipationRepository;
import katopia.fitcheck.repository.vote.VoteRepository;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VoteCommandService {

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
