package katopia.fitcheck.service.member;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.oauth2.SocialUserProfile;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFinder {

    private final MemberRepository memberRepository;

    public Member findBySocialProfileOrNull(SocialUserProfile profile) {
        return memberRepository.findByOauth2ProviderAndOauth2UserId(profile.provider(), profile.providerUserId())
                .orElse(null);
    }

    public Member findByIdOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    public Member findActiveByIdOrThrow(Long memberId) {
        return memberRepository.findByIdAndAccountStatus(memberId, AccountStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    public void requireActiveExists(Long memberId) {
        if (!memberRepository.existsByIdAndAccountStatus(memberId, AccountStatus.ACTIVE)) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER);
        }
    }

    public Member findPublicProfileByIdOrThrow(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER));
        if (member.getAccountStatus() == AccountStatus.PENDING) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_PENDING_MEMBER);
        }
        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_WITHDRAWN_MEMBER);
        }
        return member;
    }

    public Member getReferenceById(Long memberId) {
        return memberRepository.getReferenceById(memberId);
    }

}
