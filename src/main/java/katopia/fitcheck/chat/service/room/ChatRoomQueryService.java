package katopia.fitcheck.chat.service.room;

import katopia.fitcheck.chat.api.response.ChatRoomJoinedListResponse;
import katopia.fitcheck.chat.api.response.ChatRoomAllListResponse;
import katopia.fitcheck.chat.api.response.ChatRoomAllSummaryResponse;
import katopia.fitcheck.chat.api.response.ChatRoomJoinedSummaryResponse;
import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.domain.ChatRoomDocument;
import katopia.fitcheck.chat.infra.ChatMemberQueryRepository;
import katopia.fitcheck.chat.infra.ChatMemberRepository;
import katopia.fitcheck.chat.infra.ChatMessageRepository;
import katopia.fitcheck.chat.infra.ChatRoomQueryRepository;
import katopia.fitcheck.chat.infra.ChatRoomRepository;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.ChatErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatMemberQueryRepository chatMemberQueryRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;
    private final MemberFinder memberFinder;

    public ChatRoomJoinedListResponse listRooms(Long memberId, String sizeValue, String after) {
        memberFinder.findActiveByIdOrThrow(memberId);

        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        ChatRoomCursor cursor = ChatRoomCursor.decode(after);
        List<ChatMemberDocument> memberships = chatMemberQueryRepository.findJoinedRooms(memberId, size, cursor);

        Map<String, ChatRoomDocument> roomMap = loadRoomMap(memberships);
        Map<String, Long> unreadCounts = resolveUnreadCounts(memberships);
        List<ChatRoomJoinedSummaryResponse> rooms = memberships.stream()
                .map(member -> {
                    ChatRoomDocument room = requireRoom(roomMap, member.getRoomId());
                    return ChatRoomJoinedSummaryResponse.of(
                            room,
                            member,
                            unreadCounts.getOrDefault(member.getRoomId(), 0L),
                            room.getOwnerId().equals(memberId)
                    );
                })
                .toList();

        String nextCursor = ChatRoomCursor.resolveNextCursor(memberships, size);
        return ChatRoomJoinedListResponse.of(rooms, nextCursor);
    }

    public ChatRoomAllListResponse listAllRooms(Long memberId, String sizeValue, String after) {
        memberFinder.findActiveByIdOrThrow(memberId);

        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        ChatRoomAllCursor cursor = ChatRoomAllCursor.decode(after);
        List<ChatRoomDocument> rooms = chatRoomQueryRepository.findAllRooms(size, cursor);
        Set<String> joinedRoomIds = resolveJoinedRoomIds(memberId, rooms);
        List<ChatRoomAllSummaryResponse> responses = rooms.stream()
                .map(room -> ChatRoomAllSummaryResponse.from(
                        room,
                        joinedRoomIds.contains(room.getId()),
                        room.getOwnerId().equals(memberId)
                ))
                .toList();

        String nextCursor = ChatRoomAllCursor.resolveNextCursor(rooms, size);
        return ChatRoomAllListResponse.of(responses, nextCursor);
    }

    private Set<String> resolveJoinedRoomIds(Long memberId, List<ChatRoomDocument> rooms) {
        List<String> roomIds = rooms.stream()
                .map(ChatRoomDocument::getId)
                .toList();
        if (roomIds.isEmpty()) {
            return Set.of();
        }
        List<ChatMemberDocument> memberships = chatMemberRepository.findAllByMemberIdAndRoomIdIn(memberId, roomIds);
        Set<String> joinedRoomIds = new HashSet<>(memberships.size());
        for (ChatMemberDocument membership : memberships) {
            joinedRoomIds.add(membership.getRoomId());
        }
        return joinedRoomIds;
    }

    private Map<String, Long> resolveUnreadCounts(List<ChatMemberDocument> memberships) {
        if (memberships == null || memberships.isEmpty()) {
            return Map.of();
        }
        return chatMessageRepository.countUnreadMessagesByRoom(memberships);
    }

    private Map<String, ChatRoomDocument> loadRoomMap(List<ChatMemberDocument> memberships) {
        List<String> roomIds = memberships.stream()
                .map(ChatMemberDocument::getRoomId)
                .distinct()
                .toList();
        List<ChatRoomDocument> rooms = chatRoomRepository.findAllById(roomIds);
        Map<String, ChatRoomDocument> result = new LinkedHashMap<>(rooms.size());
        for (ChatRoomDocument room : rooms) {
            result.put(room.getId(), room);
        }
        return result;
    }

    private ChatRoomDocument requireRoom(Map<String, ChatRoomDocument> roomMap, String roomId) {
        ChatRoomDocument room = roomMap.get(roomId);
        if (room == null) {
            throw new BusinessException(ChatErrorCode.NOT_FOUND_CHAT_ROOM);
        }
        return room;
    }

    public record ChatRoomCursor(Instant joinedAt, String memberDocumentId) {
        private static ChatRoomCursor decode(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            try {
                String[] parts = value.split("\\|", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("invalid cursor");
                }
                return new ChatRoomCursor(Instant.parse(parts[0]), parts[1]);
            } catch (Exception ex) {
                throw new BusinessException(ChatErrorCode.INVALID_CHAT_ROOM_CURSOR);
            }
        }

        private static String encode(ChatMemberDocument member) {
            return member.getJoinedAt().toString() + "|" + member.getId();
        }

        private static String resolveNextCursor(List<ChatMemberDocument> members, int size) {
            if (members == null || members.isEmpty() || members.size() < size) {
                return null;
            }
            return encode(members.getLast());
        }
    }

    public record ChatRoomAllCursor(Instant updatedAt, String roomId) {
        private static ChatRoomAllCursor decode(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            try {
                String[] parts = value.split("\\|", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("invalid cursor");
                }
                return new ChatRoomAllCursor(Instant.parse(parts[0]), parts[1]);
            } catch (Exception ex) {
                throw new BusinessException(ChatErrorCode.INVALID_CHAT_ROOM_CURSOR);
            }
        }

        private static String encode(ChatRoomDocument room) {
            return room.getUpdatedAt().toString() + "|" + room.getId();
        }

        private static String resolveNextCursor(List<ChatRoomDocument> rooms, int size) {
            if (rooms == null || rooms.isEmpty() || rooms.size() < size) {
                return null;
            }
            return encode(rooms.getLast());
        }
    }
}
