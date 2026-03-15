package katopia.fitcheck.chat.service.message;

import katopia.fitcheck.chat.api.request.ChatMessageCreateRequest;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.domain.ChatMessageDocument;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageSequenceService chatMessageSequenceService;
    private final MemberFinder memberFinder;

    public ChatMessageResponse createMessage(Long memberId, String roomId, ChatMessageCreateRequest request) {
        Member member = memberFinder.findActiveByIdOrThrow(memberId);
        ensureRoomExists(roomId);
        ChatMemberDocument membership = requireMembership(roomId, memberId);

        String text = normalize(request.message());
        String imageObjectKey = normalize(request.imageObjectKey());
        validateMessagePayload(text, imageObjectKey);

        long messageId = chatMessageSequenceService.nextMessageId();
        ChatMessageDocument message = ChatMessageDocument.create(roomId, messageId, member, text, imageObjectKey);

        ChatMessageDocument saved = chatMessageRepository.save(message);
        membership.markLastReadMessageId(saved.getMessageId());
        chatMemberRepository.save(membership);
        return ChatMessageResponse.from(saved);
    }

    private void ensureRoomExists(String roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new BusinessException(ChatErrorCode.NOT_FOUND_CHAT_ROOM);
        }
    }

    private ChatMemberDocument requireMembership(String roomId, Long memberId) {
        return chatMemberRepository.findByRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.NOT_JOINED_CHAT_ROOM));
    }

    private void validateMessagePayload(String message, String imageObjectKey) {
        boolean hasMessage = message != null;
        boolean hasImage = imageObjectKey != null;

        if (hasMessage == hasImage) {
            throw new BusinessException(ChatErrorCode.INVALID_CHAT_MESSAGE_PAYLOAD);
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
