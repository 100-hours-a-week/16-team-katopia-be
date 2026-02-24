package katopia.fitcheck.redis.sse;

import katopia.fitcheck.global.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SseConnectionRegistry {

    private static final String KEY_PREFIX = "sse:notification:";

    private final StringRedisTemplate stringRedisTemplate;

    public List<String> register(Long memberId, String connectionId, long connectedAt) {
        String key = key(memberId);
        ZSetOperations<String, String> ops = stringRedisTemplate.opsForZSet();
        ops.add(key, connectionId, connectedAt);
        Long size = ops.zCard(key);
        if (size == null || size <= Policy.SSE_MAX_CONNECTIONS) {
            return Collections.emptyList();
        }
        List<String> evicted = new ArrayList<>();
        long excess = size - Policy.SSE_MAX_CONNECTIONS;
        for (int i = 0; i < excess; ++i) {
            ZSetOperations.TypedTuple<String> removed = ops.popMin(key);
            if (removed == null || removed.getValue() == null) {
                break;
            }
            evicted.add(removed.getValue());
        }
        return evicted;
    }

    public void remove(Long memberId, String connectionId) {
        stringRedisTemplate.opsForZSet().remove(key(memberId), connectionId);
    }

    public List<SseExpiredConnection> findExpired(long cutoffEpochMillis) {
        Set<String> keys = scanKeys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<SseExpiredConnection> expired = new ArrayList<>();
        for (String key : keys) {
            Long memberId = extractMemberId(key);
            if (memberId == null) {
                continue;
            }
            Set<String> connectionIds = stringRedisTemplate.opsForZSet()
                    .rangeByScore(key, 0, cutoffEpochMillis);
            if (connectionIds == null || connectionIds.isEmpty()) {
                continue;
            }
            for (String connectionId : connectionIds) {
                expired.add(new SseExpiredConnection(memberId, connectionId));
            }
        }
        return expired;
    }

    public void removeExpired(Long memberId, List<String> connectionIds) {
        if (connectionIds == null || connectionIds.isEmpty()) {
            return;
        }
        stringRedisTemplate.opsForZSet().remove(key(memberId), connectionIds.toArray());
    }

    public long countAllConnections() {
        Set<String> keys = scanKeys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        long total = 0;
        ZSetOperations<String, String> ops = stringRedisTemplate.opsForZSet();
        for (String key : keys) {
            Long count = ops.zCard(key);
            if (count != null) {
                total += count;
            }
        }
        return total;
    }

    private String key(Long memberId) {
        return KEY_PREFIX + "{" + memberId + "}";
    }

    private Set<String> scanKeys(String pattern) {
        Set<String> result = stringRedisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(org.springframework.data.redis.connection.RedisConnection connection) {
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
            }
        });
        return result == null ? Collections.emptySet() : result;
    }

    private Long extractMemberId(String key) {
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

    public record SseExpiredConnection(Long memberId, String connectionId) {
    }
}
