package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatRoomDocument;
import katopia.fitcheck.chat.service.room.ChatRoomQueryService;

import java.util.List;

public interface ChatRoomQueryRepository {

    List<ChatRoomDocument> findAllRooms(int size, ChatRoomQueryService.ChatRoomAllCursor cursor);
}
