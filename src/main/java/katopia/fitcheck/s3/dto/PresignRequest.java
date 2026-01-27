package katopia.fitcheck.s3.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.s3.UploadCategory;

import java.util.List;

public record PresignRequest(
        @Schema(description = "업로드 유형 (PROFILE/POST/VOTE)", example = "POST")
        UploadCategory category,
        @ArraySchema(
                arraySchema = @Schema(description = "확장자 목록 (JPG/JPEG/PNG/HEIC/WEBP)", example = "[\"jpg\",\"png\"]"),
                schema = @Schema(description = "확장자", example = "jpg")
        )
        List<String> extensions
) {
}
