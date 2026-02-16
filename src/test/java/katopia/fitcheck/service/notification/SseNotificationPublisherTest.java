package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SseNotificationPublisherTest {

    @Test
    @DisplayName("TC-NOTIFICATION-SSE-S-04 SSE 발행은 수신자 기준으로 전송")
    void tcNotificationSseS04_publish_sendsToRecipient() {
        NotificationSseService sseService = mock(NotificationSseService.class);
        SseNotificationPublisher publisher = new SseNotificationPublisher(sseService);

        Member recipient = MemberTestFactory.member(1L);
        Notification notification = Notification.of(
                recipient,
                null,
                NotificationType.VOTE_CLOSED,
                Policy.VOTE_CLOSED_MESSAGE,
                99L,
                "votes/99/cover.png"
        );

        publisher.publish(notification);

        ArgumentCaptor<Long> memberIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(sseService).send(memberIdCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertThat(memberIdCaptor.getValue()).isEqualTo(1L);
    }
}
