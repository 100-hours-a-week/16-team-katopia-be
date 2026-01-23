package katopia.fitcheck.member.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

import java.util.List;

@Schema(description = "내 정보 수정 요청")
public record MemberProfileUpdateRequest(
        @Schema(
                description = "닉네임 (한글/영문/숫자/._, 최대 20자)",
                example = SwaggerExamples.NICKNAME,
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 20,
                pattern = "^[\\p{L}\\p{N}._]+$"
        )
        String nickname,

        @Schema(
                description = "프로필 이미지 URL",
                example = SwaggerExamples.PROFILE_IMAGE_URL
        )
        String profileImageUrl,

        @Schema(
                description = "성별 (M/F)",
                example = SwaggerExamples.GENDER_M,
                allowableValues = {"M", "F"}
        )
        String gender,

        @Schema(
                description = "키(cm) 숫자 문자열 (50~300)",
                example = SwaggerExamples.HEIGHT_175,
                pattern = "^[0-9]+$"
        )
        String height,

        @Schema(
                description = "몸무게(kg) 숫자 문자열 (20~500)",
                example = SwaggerExamples.WEIGHT_70,
                pattern = "^[0-9]+$"
        )
        String weight,

        @Schema(
                description = "실시간 알림 허용 여부",
                example = SwaggerExamples.NOTIFICATION_TRUE,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Boolean enableRealtimeNotification,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "스타일 목록",
                        example = SwaggerExamples.STYLE_LIST
                ),
                schema = @Schema(
                        allowableValues = {"MINIMAL", "FEMININE", "STREET", "CASUAL", "CLASSIC", "SPORTY", "VINTAGE"}
                )
        )
        List<String> style
) { }
