package katopia.fitcheck.dto.s3;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.validation.ValidPresignRequest;
import katopia.fitcheck.service.s3.UploadCategory;

import java.util.List;

@ValidPresignRequest
public record PresignRequest(
        @Schema(
                description = Policy.PRESIGN_CATEGORY_DES,
                example = Docs.PRESIGN_CATEGORY_POST,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UploadCategory category,
        @ArraySchema(
                arraySchema = @Schema(description = Policy.PRESIGN_EXTENSIONS_DES, example = Docs.PRESIGN_EXTENSIONS_EXAMPLE),
                schema = @Schema(description = Policy.PRESIGN_EXTENSION_DES, example = Docs.PRESIGN_EXTENSION_EXAMPLE)
        )
        List<String> extensions
) {
}
