package katopia.fitcheck.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

@Schema(description = SwaggerExamples.MEMBER_SIGNUP_REQUEST_DES)
public record MemberSignupRequest(
        @Schema(
                description = SwaggerExamples.NICKNAME_DES,
                example = SwaggerExamples.NICKNAME,
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 20,
                pattern = "^[\\p{L}\\p{N}._]+$"
        )
        String nickname,
        @Schema(
                description = SwaggerExamples.GENDER_DES,
                example = SwaggerExamples.GENDER_M,
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"M", "F"}
        )
        String gender
) { }
