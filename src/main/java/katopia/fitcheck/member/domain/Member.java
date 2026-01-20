package katopia.fitcheck.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_members_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "uk_members_oauth2_user", columnNames = {"oauth2_provider", "oauth2_user_id"})
        }
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 320)
    private String email;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider", length = 20, nullable = false)
    private SocialProvider oauth2Provider;

    @Column(name = "oauth2_user_id", nullable = false)
    private Long oauth2UserId;

    @Column(name = "profile_image_url", length = 1024)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;

    @Column
    private Short height;

    @Column
    private Short weight;

    @Column(name = "enable_realtime_notification", nullable = false)
    private boolean enableRealtimeNotification;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "terms_agreed_at")
    private LocalDateTime termsAgreedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", length = 10, nullable = false)
    private AccountStatus accountStatus;

    @Builder
    private Member(Long id,
                   String email,
                   String nickname,
                   SocialProvider oauth2Provider,
                   Long oauth2UserId,
                   String profileImageUrl,
                   Gender gender,
                   Short height,
                   Short weight,
                   boolean enableRealtimeNotification,
                   LocalDateTime deletedAt,
                   LocalDateTime termsAgreedAt,
                   AccountStatus accountStatus) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.oauth2Provider = oauth2Provider;
        this.oauth2UserId = oauth2UserId;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.enableRealtimeNotification = enableRealtimeNotification;
        this.deletedAt = deletedAt;
        this.termsAgreedAt = termsAgreedAt;
        this.accountStatus = accountStatus != null ? accountStatus : AccountStatus.PENDING;
    }

    public static Member createPending(SocialProvider provider,
                                       Long providerUserId,
                                       String email
    ) {
        return Member.builder()
                .oauth2Provider(provider)
                .oauth2UserId(providerUserId)
                .email(email)
                .enableRealtimeNotification(false)
                .accountStatus(AccountStatus.PENDING)
                .build();
    }

    public void markAsWithdrawn(String anonymizedNickname) {
        this.nickname = anonymizedNickname;
        this.profileImageUrl = null;
        this.enableRealtimeNotification = false;
        this.accountStatus = AccountStatus.WITHDRAWN;
        this.deletedAt = LocalDateTime.now();
    }
}
