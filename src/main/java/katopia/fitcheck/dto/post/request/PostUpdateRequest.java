package katopia.fitcheck.dto.post.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;
import katopia.fitcheck.global.validation.PostContent;
import katopia.fitcheck.global.validation.TagList;

import java.util.List;

public record PostUpdateRequest(
    @Schema(
            description = SwaggerExamples.POST_CONTENT_UPDATE_DES,
            example = SwaggerExamples.POST_CONTENT_UPDATE,
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 200
    )
    @PostContent
    String content,

    @ArraySchema(
            arraySchema = @Schema(description = SwaggerExamples.TAG_LIST_DES, example = SwaggerExamples.TAG_LIST),
            schema = @Schema(description = SwaggerExamples.TAG_DES),
            minItems = 0,
            maxItems = 10
    )
    @TagList
    List<String> tags
) { }
