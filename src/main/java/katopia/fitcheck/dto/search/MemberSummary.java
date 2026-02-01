package katopia.fitcheck.dto.search;

import katopia.fitcheck.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberSummary(
        Long id,
        String nickname,
        String profileImageObjectKey
) {
    public static MemberSummary of(Member member) {
        return MemberSummary.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageObjectKey(member.getProfileImageObjectKey())
                .build();
    }
}
