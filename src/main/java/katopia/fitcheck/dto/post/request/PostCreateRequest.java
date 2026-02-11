package katopia.fitcheck.dto.post.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.validation.ImageObjectKeys;
import katopia.fitcheck.global.validation.PostContent;
import katopia.fitcheck.global.validation.TagList;

import java.util.List;

public record PostCreateRequest(
        @Schema(
                description = Docs.POST_CONTENT_DES,
                example = Docs.POST_CONTENT,
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = Policy.POST_CONTENT_MIN_LENGTH,
                maxLength = Policy.POST_CONTENT_MAX_LENGTH
        )
        @PostContent
        String content,

        @ArraySchema(
                arraySchema = @Schema(description = Docs.IMAGE_OBJECT_KEY_LIST_DES, example = Docs.IMAGE_OBJECT_KEY_LIST),
                schema = @Schema(description = Docs.IMAGE_OBJECT_KEY_DES),
                minItems = Policy.POST_IMAGE_MIN_COUNT,
                maxItems = Policy.POST_IMAGE_MAX_COUNT
        )
        @ImageObjectKeys(category = "POST")
        List<String> imageObjectKeys,

        @ArraySchema(
                arraySchema = @Schema(description = Policy.TAG_LIST_DES, example = Docs.TAG_LIST),
                schema = @Schema(description = Docs.TAG_DES),
                maxItems = Policy.TAG_MAX_COUNT
        )
        @TagList
        List<String> tags
) { }
