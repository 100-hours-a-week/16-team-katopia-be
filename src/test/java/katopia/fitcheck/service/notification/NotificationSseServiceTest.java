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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSseServiceTest {

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-01 SSE 연결 시 emitter 저장")
    void tcNotificationSseS01_connect_storesEmitter() {
        NotificationSseService service = new NotificationSseService();

        SseEmitter emitter = service.connect(1L, Collections.emptyList());

        Map<Long, SseEmitter> emitters = getEmitters(service);
        assertThat(emitters).containsEntry(1L, emitter);
    }

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-02 동일 사용자 연결은 이전 emitter 교체")
    void tcNotificationSseS02_connect_replacesExistingEmitter() {
        NotificationSseService service = new NotificationSseService();

        SseEmitter first = service.connect(1L, Collections.emptyList());
        SseEmitter second = service.connect(1L, Collections.emptyList());

        Map<Long, SseEmitter> emitters = getEmitters(service);
        assertThat(emitters).containsEntry(1L, second);
        assertThat(emitters.get(1L)).isNotSameAs(first);
    }

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-03 SSE 전송은 미연결 대상이면 무시")
    void tcNotificationSseS03_send_noEmitter_doesNothing() {
        NotificationSseService service = new NotificationSseService();

        service.send(999L, NotificationSummary.builder().id(1L).build());

        assertThat(getEmitters(service)).isEmpty();
    }

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-05 SSE 연결 시 미읽음 전송 시도")
    void tcNotificationSseS05_connect_sendsUnread() {
        NotificationSseService service = spy(new NotificationSseService());
        doNothing().when(service).send(eq(1L), any());

        List<NotificationSummary> unread = List.of(
                NotificationSummary.builder().id(1L).build(),
                NotificationSummary.builder().id(2L).build()
        );

        service.connect(1L, unread);

        verify(service, times(2)).send(eq(1L), any());
    }

    @SuppressWarnings("unchecked")
    private Map<Long, SseEmitter> getEmitters(NotificationSseService service) {
        return (Map<Long, SseEmitter>) ReflectionTestUtils.getField(service, "emitters");
    }
}
