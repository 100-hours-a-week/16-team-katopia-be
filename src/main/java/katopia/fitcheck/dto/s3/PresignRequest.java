package katopia.fitcheck.dto.s3;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.service.s3.UploadCategory;

import java.util.List;

public record PresignRequest(
        @Schema(description = SwaggerExamples.PRESIGN_CATEGORY_DES, example = SwaggerExamples.PRESIGN_CATEGORY_POST)
        UploadCategory category,
        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.PRESIGN_EXTENSIONS_DES, example = SwaggerExamples.PRESIGN_EXTENSIONS_EXAMPLE),
                schema = @Schema(description = SwaggerExamples.PRESIGN_EXTENSION_DES, example = SwaggerExamples.PRESIGN_EXTENSION_EXAMPLE)
        )
        List<String> extensions
) {
}
