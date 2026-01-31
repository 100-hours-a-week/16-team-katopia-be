package katopia.fitcheck.service.post;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.domain.post.Post;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {
    public void validateOwner(Post post, Long memberId) {
        if (!post.getMember().getId().equals(memberId)) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }
    }
}
