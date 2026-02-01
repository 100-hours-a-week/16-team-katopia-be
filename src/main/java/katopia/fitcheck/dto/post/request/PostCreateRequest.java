package katopia.fitcheck.dto.post.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.ImageObjectKeys;
import katopia.fitcheck.global.validation.PostContent;
import katopia.fitcheck.global.validation.TagList;

import java.util.List;

public record PostCreateRequest(
        @Schema(description = SwaggerExamples.POST_CONTENT_CREATE_DES, example = SwaggerExamples.POST_CONTENT_CREATE)
        @PostContent
        String content,

        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.IMAGE_OBJECT_KEY_LIST_DES, example = SwaggerExamples.IMAGE_OBJECT_KEY_LIST),
                schema = @Schema(description = SwaggerExamples.IMAGE_OBJECT_KEY_DES)
        )
        @ImageObjectKeys(category = "POST")
        List<String> imageObjectKeys,

        @ArraySchema(
                arraySchema = @Schema(description = SwaggerExamples.TAG_LIST_DES, example = SwaggerExamples.TAG_LIST),
                schema = @Schema(description = SwaggerExamples.TAG_DES),
                maxItems = 10
        )
        @TagList
        List<String> tags
) { }
