package katopia.fitcheck.member.dto;

import katopia.fitcheck.member.domain.Gender;
import katopia.fitcheck.member.domain.StyleType;

import java.util.Set;

public record MemberProfileUpdate(
        String nickname,
        String profileImageUrl,
        Gender gender,
        Short height,
        Short weight,
        boolean enableRealtimeNotification,
        Set<StyleType> styles
) { }
