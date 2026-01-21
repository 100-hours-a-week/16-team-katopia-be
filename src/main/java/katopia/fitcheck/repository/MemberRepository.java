package katopia.fitcheck.repository;

import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauth2ProviderAndOauth2UserId(SocialProvider provider, String oauth2UserId);

    boolean existsByNickname(String nickname);

    boolean existsByIdAndAccountStatus(Long memberId, AccountStatus status);
}
