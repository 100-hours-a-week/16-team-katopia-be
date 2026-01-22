package katopia.fitcheck.post.dto;

import katopia.fitcheck.member.domain.Member;
import lombok.Builder;

@Builder
public record PostAuthorResponse(
        String nickname,
        String profileImageUrl,
        String gender,
        Short height,
        Short weight
) {
    public static PostAuthorResponse of (Member author) {
        return PostAuthorResponse.builder()
                .nickname(author.getNickname())
                .profileImageUrl(author.getProfileImageUrl())
                .gender(author.getGender().name())
                .height(author.getHeight())
                .weight(author.getWeight())
                .build();
    }
}
