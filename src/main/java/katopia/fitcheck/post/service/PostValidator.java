package katopia.fitcheck.post.service;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.post.domain.Post;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class PostValidator {

    private static final int MAX_CONTENT_LENGTH = 200;
    private static final int MIN_IMAGE_COUNT = 1, MAX_IMAGE_COUNT = 3;
    private static final int MIN_TAG_LENGTH = 1, MAX_TAG_LENGTH = 20;

    public String validateContent(String content) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(PostErrorCode.CONTENT_REQUIRED);
        }
        String trimmed = content.trim();
        if (trimmed.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(PostErrorCode.CONTENT_TOO_LONG);
        }
        return trimmed;
    }

    public List<String> validateImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.size() < MIN_IMAGE_COUNT || imageUrls.size() > MAX_IMAGE_COUNT) {
            throw new BusinessException(PostErrorCode.IMAGE_COUNT_INVALID);
        }
        return imageUrls.stream().map(String::trim).toList();
    }

    public List<String> validateTags(List<String> tags) {
        if (tags == null) {
            return List.of();
        }
        for (String tag : tags) {
            if (!StringUtils.hasText(tag)) {
                throw new BusinessException(PostErrorCode.TAG_LENGTH_INVALID);
            }
            int length = tag.trim().length();
            if (length < MIN_TAG_LENGTH || length > MAX_TAG_LENGTH) {
                throw new BusinessException(PostErrorCode.TAG_LENGTH_INVALID);
            }
        }
        return tags.stream().map(String::trim).toList();
    }

    public void validateOwner(Post post, Long memberId) {
        if (!post.getMember().getId().equals(memberId)) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }
    }
}
