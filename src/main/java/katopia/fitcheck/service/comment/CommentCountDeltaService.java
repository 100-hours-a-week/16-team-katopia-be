package katopia.fitcheck.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCountDeltaService {

    private static final String KEY_PREFIX = "count:comment:delta:";

    private final StringRedisTemplate stringRedisTemplate;

    public void increase(Long postId) {
        stringRedisTemplate.opsForValue().increment(key(postId), 1);
    }

    public void decrease(Long postId) {
        stringRedisTemplate.opsForValue().increment(key(postId), -1);
    }

    public String key(Long postId) {
        return KEY_PREFIX + "{" + postId + "}";
    }
}
