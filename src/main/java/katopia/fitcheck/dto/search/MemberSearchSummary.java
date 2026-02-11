package katopia.fitcheck.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

@Builder
public record MemberSearchSummary(
        @Schema(description = SwaggerExamples.MEMBER_ID_DES, example = SwaggerExamples.MEMBER_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.NICKNAME_DES, example = SwaggerExamples.NICKNAME)
        String nickname,
        @Schema(description = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY)
        String profileImageObjectKey,
        @Schema(description = "팔로잉 여부", example = "false")
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
