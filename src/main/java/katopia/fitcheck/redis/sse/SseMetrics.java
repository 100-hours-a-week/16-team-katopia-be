package katopia.fitcheck.redis.sse;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseMetrics {

    private final MeterRegistry meterRegistry;
    private final SseConnectionRegistry connectionRegistry;

    @PostConstruct
    void registerMetrics() {
        Gauge.builder("sse_connection_registry_total", connectionRegistry, SseConnectionRegistry::countAllConnections)
                .register(meterRegistry);
    }
}
