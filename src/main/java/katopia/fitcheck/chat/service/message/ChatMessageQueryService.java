package katopia.fitcheck.chat.service.message;

import katopia.fitcheck.chat.api.response.ChatMessageListResponse;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.domain.ChatMessageDocument;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberFinder memberFinder;

    public ChatMessageListResponse listMessages(Long memberId, String roomId, String sizeValue, String after) {
        memberFinder.findActiveByIdOrThrow(memberId);
        ensureRoomExists(roomId);
        requireMembership(roomId, memberId);

        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        Long afterMessageId = decodeCursor(after);
        List<ChatMessageDocument> messages = chatMessageRepository.findMessages(roomId, size, afterMessageId);
        List<ChatMessageResponse> responses = messages.stream()
                .map(ChatMessageResponse::from)
                .toList();
        String nextCursor = resolveNextCursor(messages, size);
        return ChatMessageListResponse.of(responses, nextCursor);
    }

    private void ensureRoomExists(String roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new BusinessException(ChatErrorCode.NOT_FOUND_CHAT_ROOM);
        }
    }

    private void requireMembership(String roomId, Long memberId) {
        if (!chatMemberRepository.existsByRoomIdAndMemberId(roomId, memberId)) {
            throw new BusinessException(ChatErrorCode.NOT_JOINED_CHAT_ROOM);
        }
    }

    private Long decodeCursor(String after) {
        if (after == null || after.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(after);
        } catch (NumberFormatException ex) {
            throw new BusinessException(ChatErrorCode.INVALID_CHAT_MESSAGE_CURSOR);
        }
    }

    private String resolveNextCursor(List<ChatMessageDocument> messages, int size) {
        if (messages == null || messages.isEmpty() || messages.size() < size) {
            return null;
        }
        return String.valueOf(messages.get(messages.size() - 1).getMessageId());
    }
}
