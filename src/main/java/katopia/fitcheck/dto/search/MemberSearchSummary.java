package katopia.fitcheck.dto.search;

import katopia.fitcheck.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberSearchSummary(
        Long id,
        String nickname,
        String profileImageObjectKey,
        boolean isFollowing
) {
    public static MemberSearchSummary of(Member member, boolean isFollowing) {
        return MemberSearchSummary.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageObjectKey(member.getProfileImageObjectKey())
                .isFollowing(isFollowing)
                .build();
    }
}
