package katopia.fitcheck.chat.service.room;

import katopia.fitcheck.chat.api.request.ChatRoomCreateRequest;
import katopia.fitcheck.chat.api.request.ChatRoomUpdateRequest;
import katopia.fitcheck.chat.api.response.ChatRoomCreateResponse;
import katopia.fitcheck.chat.api.response.ChatRoomMembershipResponse;
import katopia.fitcheck.chat.api.response.ChatRoomUpdateResponse;
import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.domain.ChatRoomDocument;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberFinder memberFinder;

    public ChatRoomCreateResponse createRoom(Long ownerId, ChatRoomCreateRequest request) {
        memberFinder.findActiveByIdOrThrow(ownerId);

        ChatRoomDocument room = ChatRoomDocument.create(
                ownerId,
                request.title().trim(),
                normalizeThumbnail(request.thumbnailImageObjectKey())
        );
        ChatRoomDocument savedRoom = chatRoomRepository.save(room);
        ChatMemberDocument ownerMember = ChatMemberDocument.join(savedRoom.getId(), ownerId);

        try {
            chatMemberRepository.save(ownerMember);
        } catch (RuntimeException ex) {
            // Mongo 단일 문서 저장 두 건을 완전한 트랜잭션으로 묶지 못하므로 실패 시 방 문서를 보상 삭제한다.
            chatRoomRepository.deleteById(savedRoom.getId());
            throw ex;
        }

        return ChatRoomCreateResponse.from(savedRoom);
    }

    public ChatRoomMembershipResponse joinRoom(Long memberId, String roomId) {
        memberFinder.findActiveByIdOrThrow(memberId);
        ChatRoomDocument room = findRoomOrThrow(roomId);
        if (chatMemberRepository.existsByRoomIdAndMemberId(roomId, memberId)) {
            throw new BusinessException(ChatErrorCode.ALREADY_JOINED_CHAT_ROOM);
        }

        room.incrementParticipantCount();
        chatRoomRepository.save(room);
        try {
            chatMemberRepository.save(ChatMemberDocument.join(roomId, memberId));
        } catch (RuntimeException ex) {
            room.decrementParticipantCount();
            chatRoomRepository.save(room);
            throw ex;
        }
        return ChatRoomMembershipResponse.joined(room);
    }

    public ChatRoomMembershipResponse leaveRoom(Long memberId, String roomId) {
        memberFinder.findActiveByIdOrThrow(memberId);
        ChatRoomDocument room = findRoomOrThrow(roomId);
        ChatMemberDocument membership = chatMemberRepository.findByRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.NOT_JOINED_CHAT_ROOM));

        if (room.getOwnerId().equals(memberId)) {
            throw new BusinessException(ChatErrorCode.OWNER_CANNOT_LEAVE_CHAT_ROOM);
        }

        chatMemberRepository.delete(membership);
        room.decrementParticipantCount();
        chatRoomRepository.save(room);
        return ChatRoomMembershipResponse.left(room);
    }

    public ChatRoomUpdateResponse updateRoom(Long memberId, String roomId, ChatRoomUpdateRequest request) {
        memberFinder.findActiveByIdOrThrow(memberId);
        ChatRoomDocument room = findOwnedRoomOrThrow(memberId, roomId);
        room.update(request.title().trim(), normalizeThumbnail(request.thumbnailImageObjectKey()));
        ChatRoomDocument saved = chatRoomRepository.save(room);
        return ChatRoomUpdateResponse.from(saved);
    }

    public void deleteRoom(Long memberId, String roomId) {
        memberFinder.findActiveByIdOrThrow(memberId);
        ChatRoomDocument room = findOwnedRoomOrThrow(memberId, roomId);
        chatMessageRepository.deleteAllByRoomId(room.getId());
        chatMemberRepository.deleteAllByRoomId(room.getId());
        chatRoomRepository.delete(room);
    }

    private String normalizeThumbnail(String thumbnailImageObjectKey) {
        if (thumbnailImageObjectKey == null || thumbnailImageObjectKey.isBlank()) {
            return null;
        }
        return thumbnailImageObjectKey.trim();
    }

    private ChatRoomDocument findRoomOrThrow(String roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.NOT_FOUND_CHAT_ROOM));
    }

    private ChatRoomDocument findOwnedRoomOrThrow(Long memberId, String roomId) {
        ChatRoomDocument room = findRoomOrThrow(roomId);
        if (!room.getOwnerId().equals(memberId)) {
            throw new BusinessException(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        return room;
    }
}
