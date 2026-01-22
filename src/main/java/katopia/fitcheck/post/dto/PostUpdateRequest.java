package katopia.fitcheck.post.dto;

import java.util.List;

public record PostUpdateRequest(
        String content,
        List<String> imageUrls,
        List<String> tags
) { }
