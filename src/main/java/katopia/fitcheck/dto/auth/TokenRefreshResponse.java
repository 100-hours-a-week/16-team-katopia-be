package katopia.fitcheck.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;

public record TokenRefreshResponse(
        @Schema(description = Docs.AT_DES, example = Docs.AT)
        String accessToken
) { }
