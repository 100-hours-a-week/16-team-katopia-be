package katopia.fitcheck.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import lombok.Builder;

@Builder
public record MemberSearchSummary(
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Policy.NICKNAME_DES, example = Docs.NICKNAME)
        String nickname,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String profileImageObjectKey,
        @Schema(description = Docs.FOLLOW_STATUS, example = "true")
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
