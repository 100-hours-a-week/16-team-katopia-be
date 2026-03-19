package katopia.fitcheck.chat.application;

import katopia.fitcheck.chat.api.response.ChatMessageListResponse;
import katopia.fitcheck.chat.domain.ChatMessageDocument;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.chat.service.message.ChatMessageQueryService;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.support.MemberTestFactory;
import katopia.fitcheck.service.member.MemberFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageQueryServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private MemberFinder memberFinder;

    @InjectMocks
    private ChatMessageQueryService chatMessageQueryService;

    @Test
    @DisplayName("TC-CHAT-MESSAGE-S-03 메시지 목록 조회 성공(다음 커서 포함)")
    void tcChatMessageS03_listMessages_success() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        ChatMessageDocument message = ChatMessageDocument.text(
                "room-1",
                101L,
                7L,
                "핏체커",
                "profiles/7/profile.png",
                "오늘 코디 어때?"
        );

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.existsByRoomIdAndMemberId("room-1", 7L)).thenReturn(true);
        when(chatMessageRepository.findMessages("room-1", 1, null)).thenReturn(List.of(message));

        ChatMessageListResponse response = chatMessageQueryService.listMessages(7L, "room-1", "1", null);

        assertThat(response.messages()).hasSize(1);
        assertThat(response.messages().getFirst().messageId()).isEqualTo(101L);
        assertThat(response.nextCursor()).isEqualTo("101");
    }

    @Test
    @DisplayName("TC-CHAT-MESSAGE-F-03 메시지 목록 조회 실패(잘못된 커서)")
    void tcChatMessageF03_listMessages_invalidCursor() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.existsByRoomIdAndMemberId("room-1", 7L)).thenReturn(true);

        assertThatThrownBy(() -> chatMessageQueryService.listMessages(7L, "room-1", null, "abc"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.INVALID_CHAT_MESSAGE_CURSOR);
    }

    @Test
    @DisplayName("TC-CHAT-MESSAGE-F-04 메시지 목록 조회 실패(참여하지 않은 채팅방)")
    void tcChatMessageF04_listMessages_notJoined() {
        Member member = MemberTestFactory.builder(7L, "핏체커")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(memberFinder.findActiveByIdOrThrow(7L)).thenReturn(member);
        when(chatRoomRepository.existsById("room-1")).thenReturn(true);
        when(chatMemberRepository.existsByRoomIdAndMemberId("room-1", 7L)).thenReturn(false);

        assertThatThrownBy(() -> chatMessageQueryService.listMessages(7L, "room-1", null, null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.NOT_JOINED_CHAT_ROOM);
    }
}
