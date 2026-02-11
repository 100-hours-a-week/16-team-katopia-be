package katopia.fitcheck.dto.member.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.validation.GenderValue;
import katopia.fitcheck.global.validation.HeightValue;
import katopia.fitcheck.global.validation.Nickname;
import katopia.fitcheck.global.validation.StyleList;
import katopia.fitcheck.global.validation.WeightValue;

import java.util.List;

@Schema(description = Docs.MEMBER_PROFILE_UPDATE_REQUEST_DES)
public record MemberProfileUpdateRequest(
        @Schema(
                description = Policy.NICKNAME_DES,
                example = Docs.NICKNAME,
                minLength = Policy.NICKNAME_MIN_LENGTH,
                maxLength = Policy.NICKNAME_MAX_LENGTH,
                pattern = Policy.NICKNAME_REGEX
        )
        @Nickname
        String nickname,

        @Schema(
                description = Docs.IMAGE_OBJECT_KEY_DES,
                example = Docs.IMAGE_OBJECT_KEY
        )
        String profileImageObjectKey,

        @Schema(
                description = Policy.GENDER_DES,
                example = Docs.GENDER,
                allowableValues = {Policy.GENDER_M, Policy.GENDER_F}
        )
        @GenderValue
        String gender,

        @Schema(
                description = Policy.HEIGHT_DES,
                example = Docs.HEIGHT,
                pattern = "^[0-9]+$"
        )
        @HeightValue
        String height,

        @Schema(
                description = Policy.WEIGHT_DES,
                example = Docs.WEIGHT,
                pattern = "^[0-9]+$"
        )
        @WeightValue
        String weight,

        @Schema(
                description = Docs.NOTIFICATION_DES,
                example = Docs.NOTIFICATION
        )

        Boolean enableRealtimeNotification,

        @ArraySchema(
                arraySchema = @Schema(
                        description = Docs.STYLE_LIST_DES,
                        example = Docs.STYLE_LIST
                ),
                schema = @Schema(
                        allowableValues = {"MINIMAL", "FEMININE", "STREET", "CASUAL", "CLASSIC", "SPORTY", "VINTAGE"}
                ),
                maxItems = Policy.STYLE_MAX_COUNT
        )
        @StyleList
        List<String> style
) { }
