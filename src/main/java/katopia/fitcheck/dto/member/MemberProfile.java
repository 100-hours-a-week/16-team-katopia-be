package katopia.fitcheck.dto.member;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.StyleType;
import lombok.Builder;

import java.util.Set;

@Builder
public record MemberProfile(
        String nickname,
        String profileImageUrl,
        String gender,
        Short height,
        Short weight,
        Set<StyleType> style
) {
    public static MemberProfile of(Member member) {
        return MemberProfile.builder()
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .gender(member.getGender() != null ? member.getGender().name() : null)
                .height(member.getHeight())
                .weight(member.getWeight())
                .style(member.getStyles() != null ? Set.copyOf(member.getStyles()) : null)
                .build();
    }
}
