package katopia.fitcheck.service.member;

import katopia.fitcheck.domain.member.Gender;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.MemberProfileValidator;
import katopia.fitcheck.domain.member.StyleType;
import katopia.fitcheck.dto.member.request.MemberProfileUpdateRequest;
import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MemberProfileInputResolver {

    private final MemberProfileValidator profileValidator;

    public ResolvedProfile resolveForSignup(Member member, MemberSignupRequest request) {
        String nickname = request.nickname();
        String profileImageObjectKey = resolveProfileImageObjectKey(
                request.profileImageObjectKey(),
                member.getProfileImageObjectKey()
        );
        Gender gender = profileValidator.parseGender(request.gender());
        Short height = profileValidator.parseHeight(request.height());
        Short weight = profileValidator.parseWeight(request.weight());
        boolean notification = request.enableRealtimeNotification() != null
                ? request.enableRealtimeNotification()
                : member.isEnableRealtimeNotification();
        Set<StyleType> styles = normalizeStyles(profileValidator.parseStyles(request.style()));

        return new ResolvedProfile(
                nickname,
                profileImageObjectKey,
                gender,
                height,
                weight,
                notification,
                styles
        );
    }

    public ResolvedProfile resolveForUpdate(Member member, MemberProfileUpdateRequest request) {
        String nickname = request.nickname() == null ? member.getNickname() : request.nickname();
        String profileImageObjectKey = resolveProfileImageObjectKey(
                request.profileImageObjectKey(),
                member.getProfileImageObjectKey()
        );
        Gender gender = request.gender() == null ? member.getGender() : profileValidator.parseGender(request.gender());
        Short height = request.height() == null ? member.getHeight() : profileValidator.parseHeight(request.height());
        Short weight = request.weight() == null ? member.getWeight() : profileValidator.parseWeight(request.weight());
        boolean notification = request.enableRealtimeNotification() == null
                ? member.isEnableRealtimeNotification()
                : request.enableRealtimeNotification();
        Set<StyleType> styles = normalizeStyles(profileValidator.parseStyles(request.style()));

        return new ResolvedProfile(
                nickname,
                profileImageObjectKey,
                gender,
                height,
                weight,
                notification,
                styles
        );
    }

    private String resolveProfileImageObjectKey(String requestValue, String currentValue) {
        if (requestValue == null) {
            return currentValue;
        }
        String trimmed = requestValue.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Set<StyleType> normalizeStyles(Set<StyleType> parsed) {
        if (parsed == null) {
            return null;
        }
        return parsed.isEmpty() ? Collections.emptySet() : Set.copyOf(parsed);
    }

    public record ResolvedProfile(
            String nickname,
            String profileImageObjectKey,
            Gender gender,
            Short height,
            Short weight,
            boolean enableRealtimeNotification,
            Set<StyleType> styles
    ) {}
}
