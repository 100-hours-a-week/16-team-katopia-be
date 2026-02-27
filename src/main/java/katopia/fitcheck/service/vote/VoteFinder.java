package katopia.fitcheck.service.vote;

import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.repository.vote.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteFinder {

    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;

    public Vote findByIdOrThrow(Long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new BusinessException(VoteErrorCode.VOTE_NOT_FOUND));
    }

    public String findThumbnailImageObjectKey(Long voteId) {
        return voteItemRepository.findThumbnailImageObjectKeyByVoteId(voteId).orElse(null);
    }
}
