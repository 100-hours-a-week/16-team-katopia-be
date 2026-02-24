package katopia.fitcheck.service.notification;

import katopia.fitcheck.dto.notification.response.NotificationSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import katopia.fitcheck.redis.sse.SseConnectionRegistry;
import katopia.fitcheck.redis.sse.SseDisconnectPublisher;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NotificationSseServiceTest {

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-01 SSE 연결 시 emitter 저장")
    void tcNotificationSseS01_connect_storesEmitter() {
        NotificationSseService service = new NotificationSseService(
                registryReturningEmpty(),
                mock(SseDisconnectPublisher.class)
        );

        SseEmitter emitter = service.connect(1L, Collections.emptyList());

        Map<Long, Set<String>> memberConnections = getMemberConnections(service);
        assertThat(memberConnections.get(1L)).hasSize(1);
        assertThat(emitter).isNotNull();
    }

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-02 동일 사용자 연결은 여러 개 유지")
    void tcNotificationSseS02_connect_keepsMultipleEmitters() {
        NotificationSseService service = new NotificationSseService(
                registryReturningEmpty(),
                mock(SseDisconnectPublisher.class)
        );

        service.connect(1L, Collections.emptyList());
        service.connect(1L, Collections.emptyList());

        Map<Long, Set<String>> memberConnections = getMemberConnections(service);
        assertThat(memberConnections.get(1L)).hasSize(2);
    }

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-03 SSE 전송은 미연결 대상이면 무시")
    void tcNotificationSseS03_send_noEmitter_doesNothing() {
        NotificationSseService service = new NotificationSseService(
                registryReturningEmpty(),
                mock(SseDisconnectPublisher.class)
        );

        service.send(999L, NotificationSummary.builder().id(1L).build());

        assertThat(getMemberConnections(service)).isEmpty();
    }

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-05 SSE 연결 시 미읽음 전송 시도")
    void tcNotificationSseS05_connect_sendsUnread() {
        TestNotificationSseService service = new TestNotificationSseService(
                registryReturningEmpty(),
                mock(SseDisconnectPublisher.class)
        );

        List<NotificationSummary> unread = List.of(
                NotificationSummary.builder().id(1L).build(),
                NotificationSummary.builder().id(2L).build()
        );

        service.connect(1L, unread);

        assertThat(service.getSendCount()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Set<String>> getMemberConnections(NotificationSseService service) {
        return (Map<Long, Set<String>>) ReflectionTestUtils.getField(service, "memberConnections");
    }

    private SseConnectionRegistry registryReturningEmpty() {
        SseConnectionRegistry registry = mock(SseConnectionRegistry.class);
        return registry;
    }

    private static class TestNotificationSseService extends NotificationSseService {
        private int sendCount;

        private TestNotificationSseService(
                SseConnectionRegistry registry,
                SseDisconnectPublisher disconnectPublisher
        ) {
            super(registry, disconnectPublisher);
        }

        @Override
        protected void sendToConnection(String connectionId, NotificationSummary payload) {
            sendCount += 1;
        }

        private int getSendCount() {
            return sendCount;
        }
    }
}
