package katopia.fitcheck.chat.application;

import katopia.fitcheck.chat.api.response.ChatRoomJoinedListResponse;
import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.domain.ChatRoomDocument;
import katopia.fitcheck.chat.infra.ChatMemberQueryRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.chat.service.room.ChatRoomQueryService;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomQueryServiceTest {

    @Mock
    private ChatMemberQueryRepository chatMemberQueryRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MemberFinder memberFinder;

    @InjectMocks
    private ChatRoomQueryService chatRoomQueryService;

    @Test
    @DisplayName("TC-CHAT-ROOM-S-02 채팅방 목록 조회 성공(다음 커서 포함)")
    void tcChatRoomS02_listRooms_success() {
        Member member = MemberTestFactory.builder(1L, "owner")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(member);

        ChatMemberDocument roomMember = ChatMemberDocument.builder()
                .id("member-doc-1")
                .roomId("room-1")
                .memberId(1L)
                .joinedAt(Instant.parse("2026-03-11T12:34:56Z"))
                .realtimeNotificationEnabled(true)
                .lastReadMessageId(null)
                .build();
        ChatRoomDocument room = ChatRoomDocument.builder()
                .id("room-1")
                .ownerId(1L)
                .title("핏체크 같이 할 사람들")
                .participantCount(3)
                .thumbnailImageObjectKey("chat/rooms/thumb.png")
                .createdAt(Instant.parse("2026-03-11T12:34:56Z"))
                .updatedAt(Instant.parse("2026-03-11T13:00:00Z"))
                .build();

        when(chatMemberQueryRepository.findMyRooms(eq(1L), eq(1), any())).thenReturn(List.of(roomMember));
        when(chatRoomRepository.findAllById(List.of("room-1"))).thenReturn(List.of(room));

        ChatRoomJoinedListResponse response = chatRoomQueryService.listRooms(1L, "1", null);

        assertThat(response.rooms()).hasSize(1);
        assertThat(response.rooms().getFirst().roomId()).isEqualTo("room-1");
        assertThat(response.rooms().getFirst().participantCount()).isEqualTo(3);
        assertThat(response.rooms().getFirst().unreadMessageCount()).isEqualTo(0L);
        assertThat(response.nextCursor()).isEqualTo("2026-03-11T12:34:56Z|member-doc-1");
    }

    @Test
    @DisplayName("TC-CHAT-ROOM-F-02 채팅방 목록 조회 실패(잘못된 커서)")
    void tcChatRoomF02_listRooms_invalidCursor() {
        Member member = MemberTestFactory.builder(1L, "owner")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        when(memberFinder.findActiveByIdOrThrow(1L)).thenReturn(member);

        assertThatThrownBy(() -> chatRoomQueryService.listRooms(1L, null, "invalid-cursor"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.INVALID_CHAT_ROOM_CURSOR);
    }
}
