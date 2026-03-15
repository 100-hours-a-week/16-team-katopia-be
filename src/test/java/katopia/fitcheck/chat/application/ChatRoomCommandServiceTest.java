package katopia.fitcheck.chat.application;

import katopia.fitcheck.chat.api.request.ChatRoomCreateRequest;
import katopia.fitcheck.chat.api.request.ChatRoomUpdateRequest;
import katopia.fitcheck.chat.api.response.ChatRoomCreateResponse;
import katopia.fitcheck.chat.api.response.ChatRoomMembershipResponse;
import katopia.fitcheck.chat.api.response.ChatRoomUpdateResponse;
import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.domain.ChatRoomDocument;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.chat.service.room.ChatRoomCommandService;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomCommandServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private MemberFinder memberFinder;

    @InjectMocks
    private ChatRoomCommandService chatRoomCommandService;

    @Test
    @DisplayName("TC-CHAT-ROOM-S-01 채팅방 생성 성공(생성자 참여 포함)")
    void tcChatRoomS01_createRoom_success() {
        Member owner = MemberTestFactory.builder(1L, "owner")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(owner);
        when(chatRoomRepository.save(any(ChatRoomDocument.class))).thenAnswer(invocation -> {
            ChatRoomDocument room = invocation.getArgument(0);
            return ChatRoomDocument.builder()
                    .id("67d2f7c4a8b13e4d91c0ab12")
                    .ownerId(room.getOwnerId())
                    .title(room.getTitle())
                    .participantCount(room.getParticipantCount())
                    .thumbnailImageObjectKey(room.getThumbnailImageObjectKey())
                    .createdAt(room.getCreatedAt())
                    .updatedAt(room.getUpdatedAt())
                    .build();
        });
        when(chatMemberRepository.save(any(ChatMemberDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatRoomCreateResponse response = chatRoomCommandService.createRoom(
                1L,
                new ChatRoomCreateRequest("핏체크 같이 할 사람들", "chat/rooms/thumb.png")
        );

        ArgumentCaptor<ChatRoomDocument> roomCaptor = ArgumentCaptor.forClass(ChatRoomDocument.class);
        ArgumentCaptor<ChatMemberDocument> memberCaptor = ArgumentCaptor.forClass(ChatMemberDocument.class);
        verify(chatRoomRepository).save(roomCaptor.capture());
        verify(chatMemberRepository).save(memberCaptor.capture());

        ChatRoomDocument savedRoom = roomCaptor.getValue();
        ChatMemberDocument savedMember = memberCaptor.getValue();
        assertThat(savedRoom.getOwnerId()).isEqualTo(1L);
        assertThat(savedRoom.getParticipantCount()).isEqualTo(1);
        assertThat(savedRoom.getTitle()).isEqualTo("핏체크 같이 할 사람들");
        assertThat(savedMember.getRoomId()).isEqualTo("67d2f7c4a8b13e4d91c0ab12");
        assertThat(savedMember.getMemberId()).isEqualTo(1L);
        assertThat(savedMember.isRealtimeNotificationEnabled()).isTrue();
        assertThat(response.roomId()).isEqualTo("67d2f7c4a8b13e4d91c0ab12");
        assertThat(response.ownerId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-F-01 생성자 참여 저장 실패 시 방 문서 보상 삭제")
    void tcChatRoomF01_createRoom_memberSaveFails_rollsBackRoom() {
        Member owner = MemberTestFactory.builder(1L, "owner")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(owner);
        when(chatRoomRepository.save(any(ChatRoomDocument.class))).thenAnswer(invocation -> {
            ChatRoomDocument room = invocation.getArgument(0);
            return ChatRoomDocument.builder()
                    .id("67d2f7c4a8b13e4d91c0ab12")
                    .ownerId(room.getOwnerId())
                    .title(room.getTitle())
                    .participantCount(room.getParticipantCount())
                    .thumbnailImageObjectKey(room.getThumbnailImageObjectKey())
                    .createdAt(room.getCreatedAt())
                    .updatedAt(room.getUpdatedAt())
                    .build();
        });
        doThrow(new RuntimeException("mongo write failed")).when(chatMemberRepository).save(any(ChatMemberDocument.class));

        assertThatThrownBy(() -> chatRoomCommandService.createRoom(
                1L,
                new ChatRoomCreateRequest("핏체크 같이 할 사람들", null)
        )).isInstanceOf(RuntimeException.class);

        ArgumentCaptor<ChatRoomDocument> roomCaptor = ArgumentCaptor.forClass(ChatRoomDocument.class);
        verify(chatRoomRepository).save(roomCaptor.capture());
        verify(chatRoomRepository).deleteById("67d2f7c4a8b13e4d91c0ab12");
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-S-03 채팅방 참여 성공(참여 인원 수 증가)")
    void tcChatRoomS03_joinRoom_success() {
        Member member = MemberTestFactory.builder(2L, "member")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("핏체크 같이 할 사람들")
                .participantCount(1)
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();

        when(memberFinder.findActiveByIdOrThrow(2L)).thenReturn(member);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));
        when(chatMemberRepository.existsByRoomIdAndMemberId("room-1", 2L)).thenReturn(false);
        when(chatRoomRepository.save(any(ChatRoomDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(chatMemberRepository.save(any(ChatMemberDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatRoomMembershipResponse response = chatRoomCommandService.joinRoom(2L, "room-1");

        assertThat(response.roomId()).isEqualTo("room-1");
        assertThat(response.joined()).isTrue();
        assertThat(response.participantCount()).isEqualTo(2);
        verify(chatMemberRepository).save(any(ChatMemberDocument.class));
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-F-02 채팅방 참여 실패(이미 참여 중)")
    void tcChatRoomF02_joinRoom_alreadyJoined() {
        Member member = MemberTestFactory.builder(2L, "member")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("핏체크 같이 할 사람들")
                .participantCount(1)
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();

        when(memberFinder.findActiveByIdOrThrow(2L)).thenReturn(member);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));
        when(chatMemberRepository.existsByRoomIdAndMemberId("room-1", 2L)).thenReturn(true);

        assertThatThrownBy(() -> chatRoomCommandService.joinRoom(2L, "room-1"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.ALREADY_JOINED_CHAT_ROOM);
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-S-04 채팅방 퇴장 성공(참여 문서 삭제)")
    void tcChatRoomS04_leaveRoom_success() {
        Member member = MemberTestFactory.builder(2L, "member")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("핏체크 같이 할 사람들")
                .participantCount(2)
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();
        ChatMemberDocument membership = ChatMemberDocument.builder()
                .id("member-doc-1")
                .roomId("room-1")
                .memberId(2L)
                .joinedAt(java.time.Instant.parse("2026-03-11T12:10:00Z"))
                .realtimeNotificationEnabled(true)
                .build();

        when(memberFinder.findActiveByIdOrThrow(2L)).thenReturn(member);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 2L)).thenReturn(java.util.Optional.of(membership));
        when(chatRoomRepository.save(any(ChatRoomDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatRoomMembershipResponse response = chatRoomCommandService.leaveRoom(2L, "room-1");

        assertThat(response.joined()).isFalse();
        assertThat(response.participantCount()).isEqualTo(1);
        verify(chatMemberRepository).delete(membership);
        verify(chatRoomRepository).save(any(ChatRoomDocument.class));
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-F-03 채팅방 퇴장 실패(생성자 퇴장 금지)")
    void tcChatRoomF03_leaveRoom_ownerCannotLeave() {
        Member owner = MemberTestFactory.builder(1L, "owner")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("핏체크 같이 할 사람들")
                .participantCount(1)
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();
        ChatMemberDocument membership = ChatMemberDocument.builder()
                .id("member-doc-1")
                .roomId("room-1")
                .memberId(1L)
                .joinedAt(java.time.Instant.parse("2026-03-11T12:10:00Z"))
                .realtimeNotificationEnabled(true)
                .build();

        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(owner);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 1L)).thenReturn(java.util.Optional.of(membership));

        assertThatThrownBy(() -> chatRoomCommandService.leaveRoom(1L, "room-1"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.OWNER_CANNOT_LEAVE_CHAT_ROOM);
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-S-05 채팅방 수정 성공(생성자만 가능)")
    void tcChatRoomS05_updateRoom_success() {
        Member owner = MemberTestFactory.builder(1L, "owner")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("기존 채팅방")
                .participantCount(3)
                .thumbnailImageObjectKey("chat/rooms/old.png")
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();

        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(owner);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));
        when(chatRoomRepository.save(any(ChatRoomDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatRoomUpdateResponse response = chatRoomCommandService.updateRoom(
                1L,
                "room-1",
                new ChatRoomUpdateRequest("새 채팅방", "chat/rooms/new.png")
        );

        assertThat(response.roomId()).isEqualTo("room-1");
        assertThat(response.title()).isEqualTo("새 채팅방");
        assertThat(response.thumbnailImageObjectKey()).isEqualTo("chat/rooms/new.png");
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-F-04 채팅방 수정 실패(생성자가 아님)")
    void tcChatRoomF04_updateRoom_accessDenied() {
        Member member = MemberTestFactory.builder(2L, "member")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("기존 채팅방")
                .participantCount(3)
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();

        when(memberFinder.findActiveByIdOrThrow(2L)).thenReturn(member);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));

        assertThatThrownBy(() -> chatRoomCommandService.updateRoom(
                2L,
                "room-1",
                new ChatRoomUpdateRequest("새 채팅방", null)
        )).isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-S-06 채팅방 삭제 성공(메시지/참여자 정리 포함)")
    void tcChatRoomS06_deleteRoom_success() {
        Member owner = MemberTestFactory.builder(1L, "owner")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("기존 채팅방")
                .participantCount(3)
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();

        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(owner);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));

        chatRoomCommandService.deleteRoom(1L, "room-1");

        verify(chatMessageRepository).deleteAllByRoomId("room-1");
        verify(chatMemberRepository).deleteAllByRoomId("room-1");
        verify(chatRoomRepository).delete(room);
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-F-05 채팅방 삭제 실패(생성자가 아님)")
    void tcChatRoomF05_deleteRoom_accessDenied() {
        Member member = MemberTestFactory.builder(2L, "member")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("기존 채팅방")
                .participantCount(3)
                .createdAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .updatedAt(java.time.Instant.parse("2026-03-11T12:00:00Z"))
                .build();

        when(memberFinder.findActiveByIdOrThrow(2L)).thenReturn(member);
        when(chatRoomRepository.findById("room-1")).thenReturn(java.util.Optional.of(room));

        assertThatThrownBy(() -> chatRoomCommandService.deleteRoom(2L, "room-1"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }
}
