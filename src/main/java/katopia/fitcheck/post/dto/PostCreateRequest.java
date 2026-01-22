package katopia.fitcheck.post.dto;

import java.util.List;

public record PostCreateRequest(
        String content,
        List<String> imageUrls,
        List<String> tags
) { }
