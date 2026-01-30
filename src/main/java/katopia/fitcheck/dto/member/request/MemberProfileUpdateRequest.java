package katopia.fitcheck.dto.member.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.BooleanFlag;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.GenderValue;
import katopia.fitcheck.global.validation.HeightValue;
import katopia.fitcheck.global.validation.Nickname;
import katopia.fitcheck.global.validation.StyleList;
import katopia.fitcheck.global.validation.WeightValue;

import java.util.List;

@Schema(description = SwaggerExamples.MEMBER_PROFILE_UPDATE_REQUEST_DES)
public record MemberProfileUpdateRequest(
        @Schema(
                description = SwaggerExamples.NICKNAME_DES,
                example = SwaggerExamples.NICKNAME,
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 20,
                pattern = "^[\\p{L}\\p{N}._]+$"
        )
        @Nickname
        String nickname,

        @Schema(
                description = SwaggerExamples.PROFILE_IMAGE_URL_DES,
                example = SwaggerExamples.PROFILE_IMAGE_URL
        )
        String profileImageUrl,

        @Schema(
                description = SwaggerExamples.GENDER_DES,
                example = SwaggerExamples.GENDER_M,
                allowableValues = {"M", "F"}
        )
        @GenderValue(required = false)
        String gender,

        @Schema(
                description = SwaggerExamples.HEIGHT_DES,
                example = SwaggerExamples.HEIGHT_175,
                pattern = "^[0-9]+$"
        )
        @HeightValue(required = false)
        String height,

        @Schema(
                description = SwaggerExamples.WEIGHT_DES,
                example = SwaggerExamples.WEIGHT_70,
                pattern = "^[0-9]+$"
        )
        @WeightValue(required = false)
        String weight,

        @Schema(
                description = SwaggerExamples.NOTIFICATION_DES,
                example = SwaggerExamples.NOTIFICATION_TRUE,
                requiredMode = Schema.RequiredMode.REQUIRED
        )

        Boolean enableRealtimeNotification,

        @ArraySchema(
                arraySchema = @Schema(
                        description = SwaggerExamples.STYLE_LIST_DES,
                        example = SwaggerExamples.STYLE_LIST
                ),
                schema = @Schema(
                        allowableValues = {"MINIMAL", "FEMININE", "STREET", "CASUAL", "CLASSIC", "SPORTY", "VINTAGE"}
                )
        )
        @StyleList
        List<String> style
) { }
