package katopia.fitcheck.domain.member;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import katopia.fitcheck.dto.member.response.MemberAggregate;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import katopia.fitcheck.dto.member.request.MemberProfileUpdate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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

    @Column(length = Policy.NICKNAME_MAX_LENGTH, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider", length = 20, nullable = false)
    private SocialProvider oauth2Provider;

    @Column(name = "oauth2_user_id", nullable = false)
    private String oauth2UserId;

    @Column(name = "profile_image_object_key", length = Policy.IMAGE_OBJECT_KEY_MAX_LENGTH)
    private String profileImageObjectKey;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;

    @Column
    private Short height;

    @Column
    private Short weight;

    @Column(name = "enable_realtime_notification", nullable = false)
    private boolean enableRealtimeNotification;

    @Column(name = "post_count", nullable = false)
    private long postCount;

    @Column(name = "following_count", nullable = false)
    private long followingCount;

    @Column(name = "follower_count", nullable = false)
    private long followerCount;

    @ElementCollection(fetch = FetchType.LAZY, targetClass = StyleType.class)
    @CollectionTable(name = "member_styles", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "style", length = 30)
    private Set<StyleType> styles = new LinkedHashSet<>();

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
                   String oauth2UserId,
                   String profileImageObjectKey,
                   Gender gender,
                   Short height,
                   Short weight,
                   boolean enableRealtimeNotification,
                   long postCount,
                   long followingCount,
                   long followerCount,
                   Set<StyleType> styles,
                   LocalDateTime deletedAt,
                   LocalDateTime termsAgreedAt,
                   AccountStatus accountStatus) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.oauth2Provider = oauth2Provider;
        this.oauth2UserId = oauth2UserId;
        this.profileImageObjectKey = profileImageObjectKey;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.enableRealtimeNotification = enableRealtimeNotification;
        this.postCount = Math.max(0, postCount);
        this.followingCount = Math.max(0, followingCount);
        this.followerCount = Math.max(0, followerCount);
        if (styles != null) {
            this.styles.addAll(styles);
        }
        this.deletedAt = deletedAt;
        this.termsAgreedAt = termsAgreedAt;
        this.accountStatus = accountStatus != null ? accountStatus : AccountStatus.PENDING;
    }

    public static Member createPending(SocialProvider provider,
                                       String providerUserId,
                                       String email,
                                       String nickname
    ) {
        return Member.builder()
                .oauth2Provider(provider)
                .oauth2UserId(providerUserId)
                .nickname(nickname)
                .email(email)
                .enableRealtimeNotification(false)
                .postCount(0)
                .followingCount(0)
                .followerCount(0)
                .styles(Set.of())
                .accountStatus(AccountStatus.PENDING)
                .build();
    }

    public void completeRegistration(MemberSignupRequest request,
                                     Gender gender,
                                     Short height,
                                     Short weight,
                                     boolean enableRealtimeNotification,
                                     Set<StyleType> styles,
                                     String profileImageObjectKey) {
        this.nickname = request.nickname();
        this.gender = gender;
        if (height != null) {
            this.height = height;
        }
        if (weight != null) {
            this.weight = weight;
        }
        this.enableRealtimeNotification = enableRealtimeNotification;
        if (styles != null) {
            this.styles.clear();
            this.styles.addAll(styles);
        }
        if (profileImageObjectKey != null) {
            this.profileImageObjectKey = profileImageObjectKey;
        }
        this.accountStatus = AccountStatus.ACTIVE;
        this.termsAgreedAt = LocalDateTime.now();
    }

    public void markAsWithdrawn(String anonymizedNickname) {
        this.nickname = anonymizedNickname;
        this.profileImageObjectKey = null;
        this.enableRealtimeNotification = false;
        this.styles.clear();
        this.accountStatus = AccountStatus.WITHDRAWN;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return this.accountStatus == AccountStatus.WITHDRAWN;
    }

    public boolean isRejoinAllowed(Duration waitingPeriod) {
        if (waitingPeriod == null || deletedAt == null) {
            return false;
        }
        Instant withdrawnInstant = deletedAt.atZone(ZoneId.systemDefault()).toInstant();
        return Duration.between(withdrawnInstant, Instant.now()).compareTo(waitingPeriod) >= 0;
    }

    public Instant rejoinAvailableAt(Duration waitingPeriod) {
        if (waitingPeriod == null || deletedAt == null) {
            return null;
        }
        Instant withdrawnInstant = deletedAt.atZone(ZoneId.systemDefault()).toInstant();
        return withdrawnInstant.plus(waitingPeriod);
    }

    public void reopenForRejoin() {
        this.accountStatus = AccountStatus.PENDING;
        this.deletedAt = null;
        this.termsAgreedAt = null;
    }

    public void updateProfile(MemberProfileUpdate update) {
        Objects.requireNonNull(update, "update must not be null");
        this.nickname = update.nickname();
        this.profileImageObjectKey = update.profileImageObjectKey();
        this.gender = update.gender();
        this.height = update.height();
        this.weight = update.weight();
        this.enableRealtimeNotification = update.enableRealtimeNotification();
        if (update.styles() != null) {
            this.styles.clear();
            this.styles.addAll(update.styles());
        }
    }

    public MemberAggregate getAggregate() {
        return new MemberAggregate(postCount, followingCount, followerCount);
    }
}
