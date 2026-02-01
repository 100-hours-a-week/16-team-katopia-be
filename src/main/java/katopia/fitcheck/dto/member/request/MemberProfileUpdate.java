package katopia.fitcheck.dto.member.request;

import katopia.fitcheck.domain.member.Gender;
import katopia.fitcheck.domain.member.StyleType;

import java.util.Set;

public record MemberProfileUpdate(
        String nickname,
        String profileImageObjectKey,
        Gender gender,
        Short height,
        Short weight,
        boolean enableRealtimeNotification,
        Set<StyleType> styles
) { }
