package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.MemberFollow;
import katopia.fitcheck.dto.member.response.MemberFollowListResponse;
import katopia.fitcheck.dto.member.response.MemberFollowResponse;
import katopia.fitcheck.dto.member.response.MemberFollowSummary;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberFollowService {

    private final MemberFollowRepository memberFollowRepository;
    private final MemberRepository memberRepository;
    private final MemberFinder memberFinder;
    private final NotificationService notificationService;

    @Transactional
    public MemberFollowResponse follow(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new BusinessException(MemberErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }
        Member follower = memberFinder.findActiveByIdOrThrow(followerId);
        Member followed = memberFinder.findActiveByIdOrThrow(followedId);

        // TODO: 네이티브 쿼리 적용
        if (memberFollowRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            throw new BusinessException(MemberErrorCode.ALREADY_FOLLOWING);
        }
        memberFollowRepository.save(MemberFollow.of(follower, followed));

        // TODO: 집계 업데이트는 min(id)->max(id) 순으로 락 순서 고정 필요
        // TODO: 데드락 발생 시 1~2회 재시도(backoff 포함) 적용
        // TODO: 팔로워/팔로잉 집계는 Redis 기준으로 처리 후 비동기 DB 동기화로 전환
        memberRepository.incrementFollowingCount(followerId);
        memberRepository.incrementFollowerCount(followedId);
        notificationService.createFollow(followerId, followedId);

        Member target = memberFinder.findByIdOrThrow(followedId);
        return MemberFollowResponse.of(target, true);
    }

    @Transactional
    public MemberFollowResponse unfollow(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new BusinessException(MemberErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        // TODO: 네이티브 쿼리 적용
        MemberFollow follow = memberFollowRepository.findByFollowerIdAndFollowedId(followerId, followedId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOLLOWING));
        Long targetId = follow.getFollowed().getId();
        memberFollowRepository.delete(follow);

        // TODO: 집계 업데이트는 min(id)->max(id) 순으로 락 순서 고정 필요
        // TODO: 데드락 발생 시 1~2회 재시도(backoff 포함) 적용
        // TODO: 팔로워/팔로잉 집계는 Redis 기준으로 처리 후 비동기 DB 동기화로 전환
        memberRepository.decrementFollowingCount(followerId);
        memberRepository.decrementFollowerCount(targetId);
        Member target = memberFinder.findByIdOrThrow(targetId);
        return MemberFollowResponse.of(target, false);
    }

    @Transactional(readOnly = true)
    public MemberFollowListResponse listFollowers(Long memberId, String sizeValue, String after) {
        memberFinder.findPublicProfileByIdOrThrow(memberId);

        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<MemberFollowSummary> members = loadFollowers(memberId, size, after);

        String nextCursor = null;
        if (!members.isEmpty() && members.size() == size) {
            MemberFollowSummary last = members.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.createdAt(), last.followId());
        }
        return MemberFollowListResponse.of(members, nextCursor);
    }

    @Transactional(readOnly = true)
    public MemberFollowListResponse listFollowings(Long memberId, String sizeValue, String after) {
        memberFinder.findPublicProfileByIdOrThrow(memberId);

        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<MemberFollowSummary> members = loadFollowings(memberId, size, after);

        String nextCursor = null;
        if (!members.isEmpty() && members.size() == size) {
            MemberFollowSummary last = members.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.createdAt(), last.followId());
        }
        return MemberFollowListResponse.of(members, nextCursor);
    }

    private List<MemberFollowSummary> loadFollowers(Long memberId, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return memberFollowRepository.findFollowersLatest(memberId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return memberFollowRepository.findFollowersPageAfter(memberId, cursor.createdAt(), cursor.id(), pageRequest);
    }

    private List<MemberFollowSummary> loadFollowings(Long memberId, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return memberFollowRepository.findFollowingsLatest(memberId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return memberFollowRepository.findFollowingsPageAfter(memberId, cursor.createdAt(), cursor.id(), pageRequest);
    }

}
