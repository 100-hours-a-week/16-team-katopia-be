package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

import java.time.LocalDateTime;

public record MemberFollowSummary(
        @Schema(description = SwaggerExamples.FOLLOW_ID_DES, example = SwaggerExamples.FOLLOW_ID_EXAMPLE)
        Long followId,
        @Schema(description = SwaggerExamples.FOLLOW_CREATED_AT_DES, example = SwaggerExamples.FOLLOW_CREATED_AT_EXAMPLE)
        LocalDateTime createdAt,
        @Schema(description = SwaggerExamples.MEMBER_ID_DES, example = SwaggerExamples.MEMBER_ID_EXAMPLE)
        Long id,
        @Schema(description = SwaggerExamples.NICKNAME_DES, example = SwaggerExamples.NICKNAME)
        String nickname,
        @Schema(description = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY_DES, example = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY)
        String profileImageObjectKey
) {
}
