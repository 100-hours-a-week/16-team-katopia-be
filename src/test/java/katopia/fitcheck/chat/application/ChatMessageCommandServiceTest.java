package katopia.fitcheck.chat.application;

import katopia.fitcheck.chat.api.request.ChatMessageCreateRequest;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.domain.ChatMessageDocument;
import katopia.fitcheck.chat.domain.ChatMessageType;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.chat.service.message.ChatMessageCommandService;
import katopia.fitcheck.chat.service.message.ChatMessageSequenceService;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.support.MemberTestFactory;
import katopia.fitcheck.service.member.MemberFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageCommandServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatMessageSequenceService chatMessageSequenceService;

    @Mock
    private MemberFinder memberFinder;

    @InjectMocks
    private ChatMessageCommandService chatMessageCommandService;

    @Test
    @DisplayName("TC-CHAT-MESSAGE-S-01 텍스트 메시지 생성 성공(스냅샷 저장 + lastReadMessageId 갱신)")
    void tcChatMessageS01_createTextMessage_success() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .profileImageObjectKey("profiles/7/profile.png")
                .build();
        ChatMemberDocument membership = ChatMemberDocument.builder()
                .id("member-doc-1")
                .roomId("room-1")
                .memberId(7L)
                .joinedAt(Instant.parse("2026-03-13T12:00:00Z"))
                .realtimeNotificationEnabled(true)
                .lastReadMessageId(100L)
                .build();

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 7L)).thenReturn(Optional.of(membership));
        when(chatMessageSequenceService.nextMessageId()).thenReturn(101L);
        when(chatMessageRepository.save(any(ChatMessageDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(chatMemberRepository.save(any(ChatMemberDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response = chatMessageCommandService.createMessage(
                7L,
                "room-1",
                new ChatMessageCreateRequest("오늘 코디 어때?", null)
        );

        ArgumentCaptor<ChatMessageDocument> messageCaptor = ArgumentCaptor.forClass(ChatMessageDocument.class);
        verify(chatMessageRepository).save(messageCaptor.capture());
        ChatMessageDocument saved = messageCaptor.getValue();

        assertThat(saved.getMessageId()).isEqualTo(101L);
        assertThat(saved.getMessageType()).isEqualTo(ChatMessageType.TEXT);
        assertThat(saved.getSenderNicknameSnapshot()).isEqualTo("핏체커");
        assertThat(saved.getSenderProfileImageObjectKeySnapshot()).isEqualTo("profiles/7/profile.png");
        assertThat(saved.getMessage()).isEqualTo("오늘 코디 어때?");
        assertThat(saved.getImageObjectKey()).isNull();
        assertThat(response.messageId()).isEqualTo(101L);
        assertThat(membership.getLastReadMessageId()).isEqualTo(101L);
        verify(chatMemberRepository).save(membership);
    }

    @Test
    @DisplayName("TC-CHAT-MESSAGE-S-02 이미지 메시지 생성 성공(사진 스냅샷 저장)")
    void tcChatMessageS02_createImageMessage_success() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatMemberDocument membership = ChatMemberDocument.builder()
                .id("member-doc-1")
                .roomId("room-1")
                .memberId(7L)
                .joinedAt(Instant.parse("2026-03-13T12:00:00Z"))
                .realtimeNotificationEnabled(true)
                .build();

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 7L)).thenReturn(Optional.of(membership));
        when(chatMessageSequenceService.nextMessageId()).thenReturn(201L);
        when(chatMessageRepository.save(any(ChatMessageDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(chatMemberRepository.save(any(ChatMemberDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response = chatMessageCommandService.createMessage(
                7L,
                "room-1",
                new ChatMessageCreateRequest(null, "chat/messages/image.png")
        );

        assertThat(response.messageType()).isEqualTo(ChatMessageType.IMAGE);
        assertThat(response.message()).isEqualTo("사진");
        assertThat(response.imageObjectKey()).isEqualTo("chat/messages/image.png");
        assertThat(membership.getLastReadMessageId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("TC-CHAT-MESSAGE-F-01 메시지 생성 실패(텍스트/이미지 둘 다 없음)")
    void tcChatMessageF01_createMessage_withoutPayload() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatMemberDocument membership = ChatMemberDocument.builder()
                .id("member-doc-1")
                .roomId("room-1")
                .memberId(7L)
                .joinedAt(Instant.parse("2026-03-13T12:00:00Z"))
                .realtimeNotificationEnabled(true)
                .build();

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 7L)).thenReturn(Optional.of(membership));

        assertThatThrownBy(() -> chatMessageCommandService.createMessage(
                7L,
                "room-1",
                new ChatMessageCreateRequest(null, null)
        )).isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.INVALID_CHAT_MESSAGE_PAYLOAD);
    }

    @Test
    @DisplayName("TC-CHAT-MESSAGE-F-02 메시지 생성 실패(참여하지 않은 채팅방)")
    void tcChatMessageF02_createMessage_notJoined() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatMessageCommandService.createMessage(
                7L,
                "room-1",
                new ChatMessageCreateRequest("안녕", null)
        )).isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.NOT_JOINED_CHAT_ROOM);
    }
}
