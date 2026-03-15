package katopia.fitcheck.chat.service.message;

import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.chat.ws.ChatParticipantReadState;
import katopia.fitcheck.chat.ws.ChatReadStateResponse;
import katopia.fitcheck.chat.ws.ChatReadStateSnapshotResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatReadStateService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberFinder memberFinder;

    public ChatReadStateResponse acknowledge(Long memberId, String roomId, Long lastReadMessageId) {
        memberFinder.findActiveByIdOrThrow(memberId);
        validateRoomId(roomId);
        validateLastReadMessageId(lastReadMessageId);
        ensureRoomExists(roomId);

        ChatMemberDocument membership = chatMemberRepository.findByRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.NOT_JOINED_CHAT_ROOM));

        if (!chatMessageRepository.existsByRoomIdAndMessageId(roomId, lastReadMessageId)) {
            throw new BusinessException(ChatErrorCode.INVALID_CHAT_READ_STATE);
        }

        Long currentLastReadMessageId = membership.getLastReadMessageId();
        if (currentLastReadMessageId != null && currentLastReadMessageId >= lastReadMessageId) {
            return ChatReadStateResponse.of(roomId, memberId, currentLastReadMessageId);
        }

        membership.markLastReadMessageId(lastReadMessageId);
        chatMemberRepository.save(membership);
        return ChatReadStateResponse.of(roomId, memberId, membership.getLastReadMessageId());
    }

    public ChatReadStateSnapshotResponse getSnapshot(Long memberId, String roomId) {
        memberFinder.findActiveByIdOrThrow(memberId);
        validateRoomId(roomId);
        ensureRoomExists(roomId);
        chatMemberRepository.findByRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.NOT_JOINED_CHAT_ROOM));

        List<ChatParticipantReadState> participants = chatMemberRepository.findAllByRoomIdOrderByJoinedAtAsc(roomId)
                .stream()
                .map(ChatParticipantReadState::from)
                .toList();
        return ChatReadStateSnapshotResponse.of(roomId, participants);
    }

    private void ensureRoomExists(String roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new BusinessException(ChatErrorCode.NOT_FOUND_CHAT_ROOM);
        }
    }

    private void validateRoomId(String roomId) {
        if (roomId == null || roomId.isBlank()) {
            throw new BusinessException(ChatErrorCode.INVALID_CHAT_READ_STATE);
        }
    }

    private void validateLastReadMessageId(Long lastReadMessageId) {
        if (lastReadMessageId == null || lastReadMessageId <= 0L) {
            throw new BusinessException(ChatErrorCode.INVALID_CHAT_READ_STATE);
        }
    }
}
