package katopia.fitcheck.search.dto;

import katopia.fitcheck.member.domain.Member;
import lombok.Builder;

@Builder
public record MemberSummary(
        Long id,
        String nickname,
        String profileImageUrl
) {
    public static MemberSummary of(Member member) {
        return MemberSummary.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
