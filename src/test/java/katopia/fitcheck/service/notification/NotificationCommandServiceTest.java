package katopia.fitcheck.service.notification;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.notification.Notification;
import katopia.fitcheck.domain.notification.NotificationType;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostImage;
import katopia.fitcheck.domain.vote.VoteItem;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.repository.notification.NotificationRepository;
import katopia.fitcheck.repository.vote.VoteItemRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.service.post.PostFinder;
import katopia.fitcheck.support.MemberTestFactory;
import katopia.fitcheck.support.NotificationTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private static final Long NOTIFICATION_ID = 10L;
    private static final Long REFERENCE_ID = 11L;
    private static final String IMAGE_OBJECT_KEY = "posts/100/cover.png";
    private static final String VOTE_IMAGE_OBJECT_KEY = "votes/99/cover.png";

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private PostFinder postFinder;
    @Mock
    private VoteItemRepository voteItemRepository;
    @Mock
    private MemberFinder memberFinder;
    @Mock
    private NotificationRealtimePublisher realtimePublisher;

    @InjectMocks
    private NotificationCommandService notificationService;

    @Nested
    @DisplayName("Notification Command")
    class NotificationTriggerCases {

        @Test
        @DisplayName("TC-TRIGGER-S-01 팔로우 생성 시 알림 트리거")
        void tcTriggerS01_publishFollowNotification_savesNotification() {
            Member actor = MemberTestFactory.member(ACTOR_ID);
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            ReflectionTestUtils.setField(actor, "profileImageObjectKey", "profile/2/cover.png");
            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(memberFinder.findByIdOrThrow(RECIPIENT_ID)).thenReturn(recipient);

            notificationService.publishFollowNotification(ACTOR_ID, RECIPIENT_ID);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());

            Notification saved = captor.getValue();
            assertThat(saved.getNotificationType()).isEqualTo(NotificationType.FOLLOW);
            assertThat(saved.getMessage()).isEqualTo(String.format(Policy.FOLLOW_MESSAGE, actor.getNickname()));
            assertThat(saved.getRefId()).isEqualTo(ACTOR_ID);
            assertThat(saved.getImageObjectKeySnapshot()).isEqualTo(actor.getProfileImageObjectKey());
        }

        @Test
        @DisplayName("TC-TRIGGER-S-02 투표 종료 시 알림 트리거")
        void tcTriggerS02_publishVoteClosedNotification_savesNotification() {
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            when(memberFinder.findByIdOrThrow(RECIPIENT_ID)).thenReturn(recipient);
            VoteItem voteItem = mock(VoteItem.class);
            when(voteItem.getImageObjectKey()).thenReturn(VOTE_IMAGE_OBJECT_KEY);
            when(voteItemRepository.findFirstByVoteIdOrderBySortOrderAsc(99L))
                    .thenReturn(Optional.of(voteItem));

            notificationService.publishVoteClosedNotification(RECIPIENT_ID, 99L);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());

            Notification saved = captor.getValue();
            assertThat(saved.getNotificationType()).isEqualTo(NotificationType.VOTE_CLOSED);
            assertThat(saved.getMessage()).isEqualTo(Policy.VOTE_CLOSED_MESSAGE);
            assertThat(saved.getRefId()).isEqualTo(99L);
            assertThat(saved.getActorNicknameSnapshot()).isEqualTo(Policy.SYSTEM);
            assertThat(saved.getImageObjectKeySnapshot()).isEqualTo(VOTE_IMAGE_OBJECT_KEY);
        }

        @Test
        @DisplayName("TC-TRIGGER-S-06 게시글 좋아요 시 첫 이미지 스냅샷 저장")
        void tcTriggerS03_publishPostLikeNotification_savesFirstImageSnapshot() {
            Member actor = MemberTestFactory.member(ACTOR_ID);
            Post post = createPostWithImage(IMAGE_OBJECT_KEY);

            when(postFinder.findByIdOrThrow(REFERENCE_ID)).thenReturn(post);
            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(postFinder.findMemberIdByPostIdOrThrow(REFERENCE_ID)).thenReturn(RECIPIENT_ID);
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            when(memberFinder.findByIdOrThrow(RECIPIENT_ID)).thenReturn(recipient);

            notificationService.publishPostLikeNotification(ACTOR_ID, REFERENCE_ID);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());

            Notification saved = captor.getValue();
            assertThat(saved.getNotificationType()).isEqualTo(NotificationType.POST_LIKE);
            assertThat(saved.getImageObjectKeySnapshot()).isEqualTo(IMAGE_OBJECT_KEY);
            assertThat(saved.getRefId()).isEqualTo(REFERENCE_ID);
        }

        @Test
        @DisplayName("TC-TRIGGER-S-07 댓글 생성 시 첫 이미지 스냅샷 저장")
        void tcTriggerS04_publishPostCommentNotification_savesFirstImageSnapshot() {
            Member actor = MemberTestFactory.member(ACTOR_ID);
            Post post = createPostWithImage(IMAGE_OBJECT_KEY);

            when(postFinder.findByIdOrThrow(REFERENCE_ID)).thenReturn(post);
            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(postFinder.findMemberIdByPostIdOrThrow(REFERENCE_ID)).thenReturn(RECIPIENT_ID);
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            when(memberFinder.findByIdOrThrow(RECIPIENT_ID)).thenReturn(recipient);

            notificationService.publishPostCommentNotification(ACTOR_ID, REFERENCE_ID);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());

            Notification saved = captor.getValue();
            assertThat(saved.getNotificationType()).isEqualTo(NotificationType.POST_COMMENT);
            assertThat(saved.getImageObjectKeySnapshot()).isEqualTo(IMAGE_OBJECT_KEY);
            assertThat(saved.getRefId()).isEqualTo(REFERENCE_ID);
        }

        @Test
        @DisplayName("TC-TRIGGER-S-05 본인 행위는 알림 저장하지 않음")
        void tcTriggerS05_publishFollowNotification_skipsSelfNotification() {
            notificationService.publishFollowNotification(ACTOR_ID, ACTOR_ID);

            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC-TRIGGER-S-08 실시간 알림 ON이면 SSE 발행")
        void tcTriggerS08_realtimeEnabled_publishesSse() {
            Member actor = MemberTestFactory.member(ACTOR_ID);
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            ReflectionTestUtils.setField(recipient, "enableRealtimeNotification", true);
            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(memberFinder.findByIdOrThrow(RECIPIENT_ID)).thenReturn(recipient);

            notificationService.publishFollowNotification(ACTOR_ID, RECIPIENT_ID);

            verify(realtimePublisher).publish(any(Notification.class));
        }

        @Test
        @DisplayName("TC-TRIGGER-S-09 실시간 알림 OFF이면 SSE 미발행")
        void tcTriggerS09_realtimeDisabled_skipsSse() {
            Member actor = MemberTestFactory.member(ACTOR_ID);
            Member recipient = MemberTestFactory.member(RECIPIENT_ID);
            when(memberFinder.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
            when(memberFinder.findByIdOrThrow(RECIPIENT_ID)).thenReturn(recipient);

            notificationService.publishFollowNotification(ACTOR_ID, RECIPIENT_ID);

            verify(realtimePublisher, never()).publish(any(Notification.class));
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

    private Post createPostWithImage(String imageObjectKey) {
        Member author = MemberTestFactory.member(999L);
        return Post.create(author, "content", List.of(PostImage.of(1, imageObjectKey)));
    }
}
