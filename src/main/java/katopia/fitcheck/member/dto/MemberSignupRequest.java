package katopia.fitcheck.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record MemberSignupRequest(
        @Schema(
                description = "닉네임 (한글/영문/숫자/._, 최대 20자)",
                example = "fit.user_01",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 20,
                pattern = "^[\\p{L}\\p{N}._]+$"
        )
        String nickname
) { }
