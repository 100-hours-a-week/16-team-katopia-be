package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record NicknameCheckResponse(
        @Schema(description = "사용 가능 여부", example = "true")
        boolean isAvailable
) {
    public static NicknameCheckResponse of(boolean duplicated) {
        return new NicknameCheckResponse(!duplicated);
    }
}
