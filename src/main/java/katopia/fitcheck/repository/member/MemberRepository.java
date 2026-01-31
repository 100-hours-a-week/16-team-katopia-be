package katopia.fitcheck.repository.member;

import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauth2ProviderAndOauth2UserId(SocialProvider provider, String oauth2UserId);

    boolean existsByNickname(String nickname);

    boolean existsByIdAndAccountStatus(Long memberId, AccountStatus status);

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and m.nickname like concat(:nickname, '%')
            order by m.createdAt desc, m.id desc
            """)
    List<Member> searchLatestByNickname(
            @Param("nickname") String nickname,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and m.nickname like concat(:nickname, '%')
              and ((m.createdAt < :createdAt) or (m.createdAt = :createdAt and m.id < :id))
            order by m.createdAt desc, m.id desc
            """)
    List<Member> searchPageAfterByNickname(
            @Param("nickname") String nickname,
            @Param("status") AccountStatus status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );
}
