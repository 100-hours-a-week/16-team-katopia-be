package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.dto.member.request.MemberProfileUpdate;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.response.NicknameCheckResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.service.member.MemberProfileInputResolver.ResolvedProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final MemberFinder memberFinder;
    private final MemberProfileInputResolver profileInputResolver;
    private final MemberFollowRepository memberFollowRepository;

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(Long memberId, Long requesterId) {
        Member member = memberFinder.findPublicProfileByIdOrThrow(memberId);
        boolean isFollowing = requesterId != null
                && memberFollowRepository.existsByFollowerIdAndFollowedId(requesterId, memberId);
        return MemberProfileResponse.of(member, isFollowing);
    }

    @Transactional(readOnly = true)
    public MemberProfileDetailResponse getProfileDetail(Long memberId) {
        Member member = memberFinder.findActiveByIdOrThrow(memberId);
        return MemberProfileDetailResponse.of(member);
    }



    @Transactional
    public MemberProfileDetailResponse updateProfile(Long memberId, MemberProfileUpdateRequest request) {
        Member member = memberFinder.findByIdOrThrow(memberId);
        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_WITHDRAWN_MEMBER);
        }

        // nickname
        ResolvedProfile resolved = profileInputResolver.resolveForUpdate(member, request);
        String nickname = resolved.nickname();
        boolean nicknameChanged = nickname != null && !nickname.equals(member.getNickname());
        if (nicknameChanged && memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        member.updateProfile(new MemberProfileUpdate(
                resolved.nickname(),
                resolved.profileImageObjectKey(),
                resolved.gender(),
                resolved.height(),
                resolved.weight(),
                resolved.enableRealtimeNotification(),
                resolved.styles()
        ));
        memberRepository.flush();
        return MemberProfileDetailResponse.of(member);
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberFinder.findByIdOrThrow(memberId);
        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_WITHDRAWN_MEMBER);
        }
        member.markAsWithdrawn(String.format("withdrawn_%d", member.getId()));
        memberRepository.flush();
    }

    @Transactional(readOnly = true)
    public NicknameCheckResponse checkNickname(String nickname) {
        boolean duplicated = memberRepository.existsByNickname(nickname);
        return NicknameCheckResponse.of(duplicated);
    }
}
