package katopia.fitcheck.dto.s3;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PresignResponse(
        @Schema(description = "발급된 파일 목록")
        List<PresignUrl> files
) {
    public record PresignUrl(
            @Schema(description = "업로드용 presigned URL")
            String uploadUrl,
            @Schema(description = "접근용 URL")
            String accessUrl
    ) {
    }
}
