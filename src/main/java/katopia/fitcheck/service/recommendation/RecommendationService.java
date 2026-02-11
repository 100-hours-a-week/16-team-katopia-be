package katopia.fitcheck.service.recommendation;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.dto.recommendation.RecommendationMemberResponse;
import katopia.fitcheck.dto.recommendation.RecommendationResponse;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int RECOMMENDATION_LIMIT = 30;

    private final MemberFinder memberFinder;
    private final MemberRepository memberRepository;
    private final MemberFollowRepository memberFollowRepository;

    @Transactional(readOnly = true)
    public RecommendationResponse recommendUsers(Long memberId) {
        Member requester = memberFinder.findActiveByIdOrThrow(memberId);
        List<Long> followingIds = memberFollowRepository.findFollowedIdsByFollowerId(memberId);

        List<Member> candidates;
        if (followingIds.isEmpty()) {
            candidates = findLatestMembers(excludeIds(memberId, followingIds));
        } else {
            List<Long> secondDegreeIds = memberFollowRepository.findFollowedIdsByFollowerIds(followingIds);
            List<Long> candidateIds = filterCandidateIds(memberId, followingIds, secondDegreeIds);
            if (candidateIds.isEmpty()) {
                candidates = findLatestMembers(excludeIds(memberId, followingIds));
            } else {
                candidates = memberRepository.findActiveByIdsOrderByLatest(AccountStatus.ACTIVE, candidateIds);
                if (candidates.size() > RECOMMENDATION_LIMIT) {
                    candidates = candidates.subList(0, RECOMMENDATION_LIMIT);
                }
            }
        }

        List<RecommendationMemberResponse> members = candidates.stream()
                .map(RecommendationMemberResponse::of)
                .toList();
        return RecommendationResponse.of(members);
    }

    private List<Member> findLatestMembers(List<Long> excludeIds) {
        boolean excludeEmpty = excludeIds == null || excludeIds.isEmpty();
        PageRequest pageRequest = PageRequest.of(0, RECOMMENDATION_LIMIT);
        return memberRepository.findLatestActive(AccountStatus.ACTIVE, excludeEmpty, excludeIds, pageRequest);
    }

    private List<Long> filterCandidateIds(Long memberId, List<Long> followingIds, List<Long> secondDegreeIds) {
        LinkedHashSet<Long> candidates = new LinkedHashSet<>(secondDegreeIds);
        candidates.removeAll(followingIds);
        candidates.remove(memberId);
        return new ArrayList<>(candidates);
    }

    private List<Long> excludeIds(Long memberId, List<Long> followingIds) {
        LinkedHashSet<Long> excludes = new LinkedHashSet<>(followingIds);
        excludes.add(memberId);
        return new ArrayList<>(excludes);
    }
}
