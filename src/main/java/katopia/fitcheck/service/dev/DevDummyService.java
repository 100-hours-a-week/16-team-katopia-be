package katopia.fitcheck.service.dev;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.MemberFollow;
import katopia.fitcheck.dto.dev.response.DevDummyResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class DevDummyService {

    private static final String DUMMY_NICKNAME_PREFIX = "dummy";
    private static final String DUMMY_EMAIL_DOMAIN = "@local.test";
    private static final String DUMMY_PROVIDER_PREFIX = "dummy-";

    private final MemberRepository memberRepository;
    private final MemberFollowRepository memberFollowRepository;
    private final MemberFinder memberFinder;

    @Transactional
    public DevDummyResponse createDummyFollowers(int count, Long followId) {
        if (count <= 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        Member followTarget = memberFinder.findActiveByIdOrThrow(followId);
        List<Member> members = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String nickname = DUMMY_NICKNAME_PREFIX + suffix;
            String email = DUMMY_PROVIDER_PREFIX + suffix + DUMMY_EMAIL_DOMAIN;
            Member member = Member.builder()
                    .nickname(nickname)
                    .email(email)
                    .oauth2Provider(SocialProvider.KAKAO)
                    .oauth2UserId(DUMMY_PROVIDER_PREFIX + suffix)
                    .enableRealtimeNotification(false)
                    .postCount(0)
                    .followingCount(1)
                    .followerCount(0)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            members.add(member);
        }

        List<Member> saved = memberRepository.saveAll(members);
        List<MemberFollow> follows = saved.stream()
                .map(member -> MemberFollow.of(member, followTarget))
                .toList();
        memberFollowRepository.saveAll(follows);
        memberRepository.incrementFollowerCountBy(followId, count);

        return DevDummyResponse.of(saved.size(), followId);
    }
}
