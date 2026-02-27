package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.vote.Vote;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.notification.event.NotificationPayload;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.messaging.event.MessageEvent;
import katopia.fitcheck.messaging.event.MessageEventPublisher;
import katopia.fitcheck.service.notification.event.NotificationBatchEventPublisher;
import katopia.fitcheck.service.post.PostFinder;
import katopia.fitcheck.service.vote.VoteFinder;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

    private static final Long RECIPIENT_ID = 1L;
    private static final Long ACTOR_ID = 2L;
    private static final Long REFERENCE_ID = 11L;
    private static final Long COMMENT_ID = 55L;
    private static final String IMAGE_OBJECT_KEY = "posts/100/cover.png";
    private static final String VOTE_IMAGE_OBJECT_KEY = "votes/99/cover.png";
    private static final String VOTE_TITLE = "주말 룩 투표";

    @Mock
    private PostFinder postFinder;
    @Mock
    private VoteFinder voteFinder;
    @Mock
    private MemberFinder memberFinder;
    @Mock
    private MessageEventPublisher eventPublisher;
    @Mock
    private NotificationBatchEventPublisher batchEventPublisher;

    @InjectMocks
    private NotificationCommandService notificationService;

    @Nested
    @DisplayName("Notification Command")
    class NotificationTriggerCases {

        @Test
        @DisplayName("TC-TRIGGER-S-01 팔로우 생성 시 알림 트리거")
        void tcTriggerS01_publishFollowNotification_publishesEvent() {
            Member actor = MemberTestFactory.member(ACTOR_ID);
            ReflectionTestUtils.setField(actor, "profileImageObjectKey", "profile/2/cover.png");
            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);

            notificationService.publishFollowNotification(ACTOR_ID, RECIPIENT_ID);

            ArgumentCaptor<MessageEvent> captor = ArgumentCaptor.forClass(MessageEvent.class);
            verify(batchEventPublisher).publish(captor.capture());

            MessageEvent event = captor.getValue();
            assertThat(event.getEventType()).isEqualTo(NotificationType.FOLLOW.getCode());
            assertThat(event.getActorId()).isEqualTo(ACTOR_ID);
            assertThat(event.getTargetIds()).containsExactly(RECIPIENT_ID);
            assertThat(event.getRefId()).isEqualTo(ACTOR_ID);
            NotificationPayload payload = (NotificationPayload) event.getPayload();
            assertThat(payload.getActorNicknameSnapshot()).isEqualTo(actor.getNickname());
            assertThat(payload.getImageObjectKeySnapshot()).isEqualTo(actor.getProfileImageObjectKey());
        }

        @Test
        @DisplayName("TC-TRIGGER-S-02 투표 종료 시 알림 트리거")
        void tcTriggerS02_publishVoteClosedNotification_publishesEvent() {
            Vote vote = mock(Vote.class);
            when(vote.getTitle()).thenReturn(VOTE_TITLE);
            when(voteFinder.findByIdOrThrow(99L)).thenReturn(vote);
            when(voteFinder.findThumbnailImageObjectKey(99L)).thenReturn(VOTE_IMAGE_OBJECT_KEY);

            notificationService.publishVoteClosedNotification(99L);

            ArgumentCaptor<MessageEvent> captor = ArgumentCaptor.forClass(MessageEvent.class);
            verify(eventPublisher).publish(captor.capture());

            MessageEvent event = captor.getValue();
            assertThat(event.getEventType()).isEqualTo(NotificationType.VOTE_CLOSED.getCode());
            assertThat(event.getActorId()).isNull();
            assertThat(event.getTargetIds()).isNull();
            assertThat(event.getRefId()).isEqualTo(99L);
            NotificationPayload payload = (NotificationPayload) event.getPayload();
            assertThat(payload.getImageObjectKeySnapshot()).isEqualTo(VOTE_IMAGE_OBJECT_KEY);
            assertThat(payload.getMessageArgs()).containsExactly(VOTE_TITLE);
        }

        @Test
        @DisplayName("TC-TRIGGER-S-06 게시글 좋아요 시 첫 이미지 스냅샷 저장")
        void tcTriggerS03_publishPostLikeNotification_publishesEvent() {
            Member actor = MemberTestFactory.member(ACTOR_ID);

            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(postFinder.findMemberIdByPostIdOrThrow(REFERENCE_ID)).thenReturn(RECIPIENT_ID);
            when(postFinder.findThumbnailImageObjectKey(REFERENCE_ID)).thenReturn(IMAGE_OBJECT_KEY);

            notificationService.publishPostLikeNotification(ACTOR_ID, REFERENCE_ID);

            ArgumentCaptor<MessageEvent> captor = ArgumentCaptor.forClass(MessageEvent.class);
            verify(batchEventPublisher).publish(captor.capture());

            MessageEvent event = captor.getValue();
            assertThat(event.getEventType()).isEqualTo(NotificationType.POST_LIKE.getCode());
            assertThat(event.getRefId()).isEqualTo(REFERENCE_ID);
            NotificationPayload payload = (NotificationPayload) event.getPayload();
            assertThat(payload.getImageObjectKeySnapshot()).isEqualTo(IMAGE_OBJECT_KEY);
        }

        @Test
        @DisplayName("TC-TRIGGER-S-08 게시글 작성 시 팔로워 알림 트리거")
        void tcTriggerS08_publishPostCreatedNotification_publishesEvent() {
            Member actor = MemberTestFactory.member(ACTOR_ID);

            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(postFinder.findThumbnailImageObjectKey(REFERENCE_ID)).thenReturn(IMAGE_OBJECT_KEY);

            notificationService.publishPostCreatedNotification(ACTOR_ID, REFERENCE_ID);

            ArgumentCaptor<MessageEvent> captor = ArgumentCaptor.forClass(MessageEvent.class);
            verify(eventPublisher).publish(captor.capture());

            MessageEvent event = captor.getValue();
            assertThat(event.getEventType()).isEqualTo(NotificationType.POST_CREATED.getCode());
            assertThat(event.getActorId()).isEqualTo(ACTOR_ID);
            assertThat(event.getTargetIds()).isNull();
            assertThat(event.getRefId()).isEqualTo(REFERENCE_ID);
            NotificationPayload payload = (NotificationPayload) event.getPayload();
            assertThat(payload.getActorNicknameSnapshot()).isEqualTo(actor.getNickname());
            assertThat(payload.getImageObjectKeySnapshot()).isEqualTo(IMAGE_OBJECT_KEY);
        }

        @Test
        @DisplayName("TC-TRIGGER-S-07 댓글 생성 시 첫 이미지 스냅샷 저장")
        void tcTriggerS04_publishPostCommentNotification_publishesEvent() {
            Member actor = MemberTestFactory.member(ACTOR_ID);

            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(postFinder.findMemberIdByPostIdOrThrow(REFERENCE_ID)).thenReturn(RECIPIENT_ID);
            when(postFinder.findThumbnailImageObjectKey(REFERENCE_ID)).thenReturn(IMAGE_OBJECT_KEY);

            notificationService.publishPostCommentNotification(ACTOR_ID, REFERENCE_ID, COMMENT_ID);

            ArgumentCaptor<MessageEvent> captor = ArgumentCaptor.forClass(MessageEvent.class);
            verify(batchEventPublisher).publish(captor.capture());

            MessageEvent event = captor.getValue();
            assertThat(event.getEventType()).isEqualTo(NotificationType.POST_COMMENT.getCode());
            assertThat(event.getRefId()).isEqualTo(COMMENT_ID);
            NotificationPayload payload = (NotificationPayload) event.getPayload();
            assertThat(payload.getImageObjectKeySnapshot()).isEqualTo(IMAGE_OBJECT_KEY);
        }

        @Test
        @DisplayName("TC-TRIGGER-S-05 본인 행위는 알림 저장하지 않음")
        void tcTriggerS05_publishFollowNotification_skipsSelfNotification() {
            notificationService.publishFollowNotification(ACTOR_ID, ACTOR_ID);

            verify(batchEventPublisher, never()).publish(any(MessageEvent.class));
        }
    }

}
