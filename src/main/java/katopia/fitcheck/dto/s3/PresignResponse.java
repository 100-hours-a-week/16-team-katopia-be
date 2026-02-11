package katopia.fitcheck.dto.s3;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PresignResponse(
        @Schema(description = "발급된 파일 목록")
        List<PresignUrl> files
) {
    public static PresignResponse of(List<PresignUrl> files) {
        return new PresignResponse(files);
    }

    public record PresignUrl(
            @Schema(description = "업로드용 presigned URL", example = "https://s3.ap-northeast-2.amazonaws.com/bucket/key?X-Amz-...")
            String uploadUrl,
            @Schema(description = "이미지 오브젝트 키", example = "posts/1/1700000000000-uuid.png")
            String imageObjectKey
    ) {
    }
}
