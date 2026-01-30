package katopia.fitcheck.dto.post.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

import java.util.List;

public record PostCreateRequest(
        @Schema(description = SwaggerExamples.POST_CONTENT_CREATE_DES, example = SwaggerExamples.POST_CONTENT_CREATE)
        String content,

        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.IMAGE_URL_LIST_DES, example = SwaggerExamples.IMAGE_URL_LIST),
                schema = @Schema(description = SwaggerExamples.IMAGE_URL_DES)
        )
        List<String> imageUrls,

        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.TAG_LIST_DES, example = SwaggerExamples.TAG_LIST),
                schema = @Schema(description = SwaggerExamples.TAG_DES)
        )
        List<String> tags
) { }
