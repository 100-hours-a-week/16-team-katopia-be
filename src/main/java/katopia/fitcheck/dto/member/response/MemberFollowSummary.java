package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;

import java.time.LocalDateTime;

public record MemberFollowSummary(
        @Schema(description = Docs.ID_DES, example = "1")
        Long followId,
        @Schema(description = Docs.CREATED_AT_DES, example = Docs.TIMESTAMP)
        LocalDateTime createdAt,
        @Schema(description = Docs.ID_DES, example = "1")
        Long id,
        @Schema(description = Policy.NICKNAME_DES, example = Docs.NICKNAME)
        String nickname,
        @Schema(description = Docs.IMAGE_OBJECT_KEY_DES, example = Docs.IMAGE_OBJECT_KEY)
        String profileImageObjectKey
) {
}
