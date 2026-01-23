package katopia.fitcheck.member.repository;

import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Member;
import katopia.fitcheck.member.domain.Gender;
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

    Optional<Member> findByIdAndAccountStatus(Long memberId, AccountStatus status);

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and m.nickname like concat(:nickname, '%')
              and (:gender is null or m.gender = :gender)
              and (:minHeight is null or (m.height is not null and m.height between :minHeight and :maxHeight))
              and (:minWeight is null or (m.weight is not null and m.weight between :minWeight and :maxWeight))
            order by m.createdAt desc, m.id desc
            """)
    List<Member> searchLatestByNickname(
            @Param("nickname") String nickname,
            @Param("status") AccountStatus status,
            @Param("minHeight") Short minHeight,
            @Param("maxHeight") Short maxHeight,
            @Param("minWeight") Short minWeight,
            @Param("maxWeight") Short maxWeight,
            @Param("gender") Gender gender,
            Pageable pageable
    );

    @Query("""
            select m from Member m
            where m.accountStatus = :status
              and m.nickname like concat(:nickname, '%')
              and (:gender is null or m.gender = :gender)
              and (:minHeight is null or (m.height is not null and m.height between :minHeight and :maxHeight))
              and (:minWeight is null or (m.weight is not null and m.weight between :minWeight and :maxWeight))
              and ((m.createdAt < :createdAt) or (m.createdAt = :createdAt and m.id < :id))
            order by m.createdAt desc, m.id desc
            """)
    List<Member> searchPageAfterByNickname(
            @Param("nickname") String nickname,
            @Param("status") AccountStatus status,
            @Param("minHeight") Short minHeight,
            @Param("maxHeight") Short maxHeight,
            @Param("minWeight") Short minWeight,
            @Param("maxWeight") Short maxWeight,
            @Param("gender") Gender gender,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );
}
