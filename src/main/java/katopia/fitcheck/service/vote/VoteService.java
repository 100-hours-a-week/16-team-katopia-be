package katopia.fitcheck.service.vote;

import katopia.fitcheck.dto.vote.request.VoteCreateRequest;
import katopia.fitcheck.dto.vote.request.VoteParticipationRequest;
import katopia.fitcheck.dto.vote.response.VoteCandidateResponse;
import katopia.fitcheck.dto.vote.response.VoteCreateResponse;
import katopia.fitcheck.dto.vote.response.VoteListResponse;
import katopia.fitcheck.dto.vote.response.VoteResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteCommandService voteCommandService;
    private final VoteQueryService voteQueryService;

    @Transactional
    public VoteCreateResponse create(Long memberId, VoteCreateRequest request) {
        return voteCommandService.create(memberId, request);
    }

    @Transactional(readOnly = true)
    public VoteListResponse listMine(Long memberId, String sizeValue, String after) {
        return voteQueryService.listMine(memberId, sizeValue, after);
    }

    @Transactional(readOnly = true)
    public VoteCandidateResponse findLatestCandidate(Long memberId) {
        return voteQueryService.findLatestCandidate(memberId);
    }

    @Transactional
    public VoteResultResponse participate(Long memberId, Long voteId, VoteParticipationRequest request) {
        return voteCommandService.participate(memberId, voteId, request);
    }

    @Transactional(readOnly = true)
    public VoteResultResponse getResult(Long memberId, Long voteId) {
        return voteQueryService.getResult(memberId, voteId);
    }

    @Transactional
    public void delete(Long memberId, Long voteId) {
        voteCommandService.delete(memberId, voteId);
    }
}
