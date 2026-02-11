package katopia.fitcheck.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.GenderValue;
import katopia.fitcheck.global.validation.HeightValue;
import katopia.fitcheck.global.validation.Nickname;
import katopia.fitcheck.global.validation.StyleList;
import katopia.fitcheck.global.validation.WeightValue;

import java.util.List;

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
        String gender,

        @Schema(
                description = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY_DES,
                example = SwaggerExamples.PROFILE_IMAGE_OBJECT_KEY,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String profileImageObjectKey,

        @Schema(
                description = SwaggerExamples.HEIGHT_DES,
                example = SwaggerExamples.HEIGHT_175,
                pattern = "^[0-9]+$",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @HeightValue
        String height,

        @Schema(
                description = SwaggerExamples.WEIGHT_DES,
                example = SwaggerExamples.WEIGHT_70,
                pattern = "^[0-9]+$",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @WeightValue
        String weight,

        @Schema(
                description = SwaggerExamples.NOTIFICATION_DES,
                example = SwaggerExamples.NOTIFICATION_TRUE,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Boolean enableRealtimeNotification,

        @io.swagger.v3.oas.annotations.media.ArraySchema(
                arraySchema = @Schema(
                        description = SwaggerExamples.STYLE_LIST_DES,
                        example = SwaggerExamples.STYLE_LIST
                ),
                schema = @Schema(
                        allowableValues = {"MINIMAL", "FEMININE", "STREET", "CASUAL", "CLASSIC", "SPORTY", "VINTAGE"}
                ),
                minItems = 0,
                maxItems = 2
        )
        @StyleList
        List<String> style
) { }
