package katopia.fitcheck.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.GenderValue;
import katopia.fitcheck.global.validation.Nickname;

@Schema(description = SwaggerExamples.MEMBER_SIGNUP_REQUEST_DES)
public record MemberSignupRequest(
        @Schema(
                description = SwaggerExamples.NICKNAME_DES,
                example = SwaggerExamples.NICKNAME,
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 20,
                pattern = "^[\\p{L}\\p{N}._]+$"
        )
        @Nickname(required = true)
        String nickname,
        @Schema(
                description = SwaggerExamples.GENDER_DES,
                example = SwaggerExamples.GENDER_M,
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"M", "F"}
        )
        @GenderValue(required = true)
        String gender
) { }
