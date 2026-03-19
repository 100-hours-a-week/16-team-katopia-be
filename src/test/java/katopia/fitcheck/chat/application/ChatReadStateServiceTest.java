package katopia.fitcheck.chat.application;

import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.chat.service.message.ChatReadStateService;
import katopia.fitcheck.chat.ws.ChatReadStateSnapshotResponse;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatReadStateServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private MemberFinder memberFinder;

    @InjectMocks
    private ChatReadStateService chatReadStateService;

    @Test
    @DisplayName("TC-CHAT-READ-S-01 읽음 상태 스냅샷 조회 성공")
    void tcChatReadS01_getSnapshot_success() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatMemberDocument me = ChatMemberDocument.join("room-1", 7L);
        me.markLastReadMessageId(101L);
        ChatMemberDocument other = ChatMemberDocument.join("room-1", 8L);
        other.markLastReadMessageId(99L);

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 7L)).thenReturn(Optional.of(me));
        when(chatMemberRepository.findAllByRoomIdOrderByJoinedAtAsc("room-1")).thenReturn(List.of(me, other));

        ChatReadStateSnapshotResponse response = chatReadStateService.getSnapshot(7L, "room-1");

        assertThat(response.roomId()).isEqualTo("room-1");
        assertThat(response.participants()).hasSize(2);
        assertThat(response.participants().get(0).memberId()).isEqualTo(7L);
        assertThat(response.participants().get(0).lastReadMessageId()).isEqualTo(101L);
        assertThat(response.participants().get(1).memberId()).isEqualTo(8L);
        assertThat(response.participants().get(1).lastReadMessageId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("TC-CHAT-READ-F-01 읽음 상태 스냅샷 조회 실패(참여하지 않은 채팅방)")
    void tcChatReadF01_getSnapshot_notJoined() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.findByRoomIdAndMemberId("room-1", 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatReadStateService.getSnapshot(7L, "room-1"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.NOT_JOINED_CHAT_ROOM);
    }
}
