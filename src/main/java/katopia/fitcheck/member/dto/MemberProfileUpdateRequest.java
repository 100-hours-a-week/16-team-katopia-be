package katopia.fitcheck.member.dto;

import java.util.List;

public record MemberProfileUpdateRequest(
        String nickname,
        String profileImageUrl,
        String gender,
        String height,
        String weight,
        Boolean enableRealtimeNotification,
        List<String> style
) { }
