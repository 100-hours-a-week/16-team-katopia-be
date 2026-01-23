package katopia.fitcheck.post.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.SwaggerExamples;

import java.util.List;

public record PostUpdateRequest(
        @Schema(description = "게시글 본문", example = SwaggerExamples.POST_CONTENT_UPDATE)
        String content,

        @ArraySchema(
                arraySchema = @Schema(description = "이미지 URL 목록", example = SwaggerExamples.IMAGE_URL_LIST),
                schema = @Schema(description = "이미지 URL")
        )
        List<String> imageUrls,

        @ArraySchema(
                arraySchema = @Schema(description = "태그 목록", example = SwaggerExamples.TAG_LIST),
                schema = @Schema(description = "태그")
        )
        List<String> tags
) { }
