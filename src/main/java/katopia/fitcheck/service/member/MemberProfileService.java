package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.*;
import katopia.fitcheck.dto.member.request.MemberProfileUpdate;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.response.MemberProfileDetailResponse;
import katopia.fitcheck.dto.member.response.MemberProfileResponse;
import katopia.fitcheck.dto.member.response.NicknameCheckResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final MemberFinder memberFinder;
    private final MemberProfileValidator profileValidator;

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(Long memberId) {
        Member member = memberFinder.findPublicProfileByIdOrThrow(memberId);
        return MemberProfileResponse.of(member);
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
        String nickname = request.nickname();
        boolean nicknameChanged = !nickname.equals(member.getNickname());
        if (nicknameChanged && memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        // gender, height, weight, styles, notification
        Gender gender = profileValidator.parseGender(request.gender());
        Short height = profileValidator.parseHeight(request.height());
        Short weight = profileValidator.parseWeight(request.weight());
        Set<StyleType> styles = resolveStyles(request);
        boolean notification = request.enableRealtimeNotification();

        member.updateProfile(new MemberProfileUpdate(
                nickname,
                normalizeImageUrl(request.profileImageUrl()),
                gender,
                height,
                weight,
                notification,
                styles
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


    private Set<StyleType> resolveStyles(MemberProfileUpdateRequest request) {
        Set<StyleType> parsed = profileValidator.parseStyles(request.style());
        if (parsed == null) {
            return null;
        }
        return parsed.isEmpty() ? Collections.emptySet() : Set.copyOf(parsed);
    }

    private String normalizeImageUrl(String profileImageUrl) {
        return StringUtils.hasText(profileImageUrl) ? profileImageUrl.trim() : null;
    }

    @Transactional(readOnly = true)
    public NicknameCheckResponse checkNickname(String nickname) {
        boolean duplicated = memberRepository.existsByNickname(nickname);
        return NicknameCheckResponse.of(duplicated);
    }
}
