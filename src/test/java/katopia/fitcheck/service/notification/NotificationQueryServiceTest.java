package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.dto.notification.response.NotificationListResponse;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.NotificationErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.repository.notification.NotificationRepository;
import katopia.fitcheck.support.MemberTestFactory;
import katopia.fitcheck.support.NotificationTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    private static final Long RECIPIENT_ID = 1L;
    private static final Long ACTOR_ID = 2L;
    private static final Long NOTIFICATION_ID = 10L;
    private static final Long REFERENCE_ID = 11L;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationQueryService queryService;

    @Nested
    @DisplayName("Notification Query")
    class NotificationQueryCases {

        @Test
        @DisplayName("TC-NOTIFICATION-S-01 알림 읽음 처리 성공")
        void tcNotificationS01_markRead_updatesReadAt() {
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            Notification notification = createNotification(REFERENCE_ID, recipient, LocalDateTime.now());

            when(notificationRepository.findByIdAndRecipientId(NOTIFICATION_ID, RECIPIENT_ID)).thenReturn(Optional.of(notification));

            NotificationSummary summary = queryService.markRead(RECIPIENT_ID, NOTIFICATION_ID);

            assertThat(summary.readAt()).isNotNull();
        }

        @Test
        @DisplayName("TC-NOTIFICATION-F-01 알림 읽음 처리 실패(알림 없음)")
        void tcNotificationF01_markRead_failsWhenMissing() {
            when(notificationRepository.findByIdAndRecipientId(NOTIFICATION_ID, RECIPIENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> queryService.markRead(RECIPIENT_ID, NOTIFICATION_ID))
                    .isInstanceOf(BusinessException.class)
                    .extracting(ex -> ((BusinessException) ex).getErrorCode())
                    .isEqualTo(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("TC-NOTIFICATION-F-02 알림 읽음 처리 실패(이미 읽음)")
        void tcNotificationF02_markRead_failsWhenAlreadyRead() {
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            Notification notification = createNotification(REFERENCE_ID, recipient, LocalDateTime.now());
            ReflectionTestUtils.setField(notification, "readAt", LocalDateTime.now());

            when(notificationRepository.findByIdAndRecipientId(NOTIFICATION_ID, RECIPIENT_ID)).thenReturn(Optional.of(notification));

            assertThatThrownBy(() -> queryService.markRead(RECIPIENT_ID, NOTIFICATION_ID))
                    .isInstanceOf(BusinessException.class)
                    .extracting(ex -> ((BusinessException) ex).getErrorCode())
                    .isEqualTo(NotificationErrorCode.NOTIFICATION_ALREADY_READ);
        }

        @Test
        @DisplayName("TC-NOTIFICATION-S-02 미읽음 알림 목록 조회 성공")
        void tcNotificationS02_getLatestUnread_returnsSummaries() {
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            Notification notification = createNotification(REFERENCE_ID, recipient, LocalDateTime.now());

            when(notificationRepository.findLatestUnreadByRecipientId(eq(RECIPIENT_ID), eq(PageRequest.of(0, 10))))
                    .thenReturn(List.of(notification));

            List<NotificationSummary> summaries = queryService.getLatestUnread(RECIPIENT_ID, 10);

            assertThat(summaries).hasSize(1);
            assertThat(summaries.getFirst().id()).isEqualTo(notification.getId());
        }
    }

    @Nested
    class NotificationPagingCases {

        @Test
        @DisplayName("TC-PAGE-S-04 목록 nextCursor 생성")
        void tcPageS04_list_buildsNextCursor() {
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            LocalDateTime latestCreatedAt = LocalDateTime.parse(Docs.SECOND_TIME);
            LocalDateTime olderCreatedAt = LocalDateTime.parse(Docs.FIRST_TIME);
            Notification latest = createNotification(NOTIFICATION_ID + 1L, recipient, latestCreatedAt);
            Notification older = createNotification(NOTIFICATION_ID, recipient, olderCreatedAt);

            when(notificationRepository.findLatestByRecipientId(eq(RECIPIENT_ID), any(PageRequest.class)))
                    .thenReturn(List.of(latest, older));

            NotificationListResponse response = queryService.getList(RECIPIENT_ID, "2", null);

            assertThat(response.notifications()).hasSize(2);
            assertThat(response.nextCursor()).isEqualTo(CursorPagingHelper.encodeCursor(olderCreatedAt, NOTIFICATION_ID));
        }

        @Test
        @DisplayName("TC-PAGE-S-05 커서 이후 페이지 조회")
        void tcPageS05_list_usesAfterCursor() {
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            LocalDateTime cursorCreatedAt = LocalDateTime.parse(Docs.FIRST_TIME);
            Notification notification = createNotification(NOTIFICATION_ID, recipient, cursorCreatedAt.plusSeconds(1));

            when(notificationRepository.findPageAfter(eq(RECIPIENT_ID), any(), any(), any(PageRequest.class)))
                    .thenReturn(List.of(notification));

            queryService.getList(RECIPIENT_ID, "1", CursorPagingHelper.encodeCursor(cursorCreatedAt, NOTIFICATION_ID));

            verify(notificationRepository).findPageAfter(
                    eq(RECIPIENT_ID),
                    eq(cursorCreatedAt),
                    eq(NOTIFICATION_ID),
                    eq(PageRequest.of(0, 1))
            );
        }
    }

    private Notification createNotification(Long id, Member recipient, LocalDateTime createdAt) {
        Member actor = MemberTestFactory.member(ACTOR_ID);
        return NotificationTestFactory.notification(
                id,
                recipient,
                actor,
                NotificationType.POST_LIKE,
                String.format(Policy.POST_LIKE_MESSAGE, actor.getNickname()),
                REFERENCE_ID,
                actor.getProfileImageObjectKey(),
                createdAt,
                null
        );
    }
}
