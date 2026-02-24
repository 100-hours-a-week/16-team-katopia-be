package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.aggregation.CountBatch;
import katopia.fitcheck.repository.aggregation.CountBatchRepository;
import katopia.fitcheck.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCountBatchWorker {

    private static final String KEY_PREFIX = "count:comment:delta:";

    private final StringRedisTemplate stringRedisTemplate;
    private final PostRepository postRepository;
    private final CountBatchRepository countBatchRepository;

    @Scheduled(fixedDelayString = "#{T(katopia.fitcheck.global.policy.Policy).COMMENT_COUNT_BATCH_INTERVAL.toMillis()}")
    public void syncCommentCounts() {
        Set<String> keys = scanKeys(KEY_PREFIX + "*");
        if (keys.isEmpty()) {
            return;
        }
        int appliedPostCount = 0;
        long appliedDelta = 0;
        for (String key : keys) {
            Long postId = extractPostId(key);
            if (postId == null) {
                continue;
            }
            long delta = getAndResetDelta(key);
            if (delta == 0) {
                continue;
            }
            try {
                int updated = postRepository.applyCommentCountDelta(postId, delta);
                if (updated > 0) {
                    appliedPostCount += 1;
                    appliedDelta += delta;
                }
            } catch (DataAccessException ex) {
                restoreDelta(key, delta);
                log.debug("Failed to apply comment delta. postId={}, delta={}", postId, delta, ex);
            }
        }
        countBatchRepository.save(CountBatch.of(LocalDateTime.now(), LocalDateTime.now(), appliedPostCount, (int) appliedDelta));
    }

    private long getAndResetDelta(String key) {
        String value = stringRedisTemplate.opsForValue().getAndSet(key, "0");
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void restoreDelta(String key, long delta) {
        stringRedisTemplate.opsForValue().increment(key, delta);
    }

    private Set<String> scanKeys(String pattern) {
        Set<String> result = stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> collected = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(200)
                    .build())) {
                while (cursor.hasNext()) {
                    collected.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (DataAccessException ex) {
                return Collections.emptySet();
            }
            return collected;
        });
        return result == null ? Collections.emptySet() : result;
    }

    private Long extractPostId(String key) {
        if (!key.startsWith(KEY_PREFIX)) {
            return null;
        }
        String raw = key.substring(KEY_PREFIX.length());
        if (raw.startsWith("{") && raw.endsWith("}")) {
            raw = raw.substring(1, raw.length() - 1);
        }
        if (raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
