package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatMemberDocument;
import katopia.fitcheck.chat.service.room.ChatRoomQueryService;

import java.util.List;

public interface ChatMemberQueryRepository {

    List<ChatMemberDocument> findJoinedRooms(Long memberId, int size, ChatRoomQueryService.ChatRoomCursor cursor);
}
