package katopia.fitcheck.member.service;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.oauth2.SocialUserProfile;
import katopia.fitcheck.member.repository.MemberRepository;
import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Member;
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
        return memberRepository.findById(memberId)
                .filter(member -> member.getAccountStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    public Member getReferenceById(Long memberId) {
        return memberRepository.getReferenceById(memberId);
    }

}
