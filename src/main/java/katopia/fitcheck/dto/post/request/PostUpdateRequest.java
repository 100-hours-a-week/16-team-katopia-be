package katopia.fitcheck.dto.post.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.validation.PostContent;
import katopia.fitcheck.global.validation.TagList;

import java.util.List;

public record PostUpdateRequest(
    @Schema(
            description = Docs.POST_CONTENT_UPDATE_DES,
            example = Docs.POST_CONTENT_UPDATE,
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = Policy.POST_CONTENT_MIN_LENGTH,
            maxLength = Policy.POST_CONTENT_MAX_LENGTH
    )
    @PostContent
    String content,

    @ArraySchema(
            arraySchema = @Schema(description = Policy.TAG_LIST_DES, example = Docs.TAG_LIST),
            schema = @Schema(description = Docs.TAG_DES),
            maxItems = Policy.TAG_MAX_COUNT
    )
    @TagList
    List<String> tags
) { }
