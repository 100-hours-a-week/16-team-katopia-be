package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.application.ChatRoomQueryService.ChatRoomAllCursor;
import katopia.fitcheck.chat.domain.ChatRoomDocument;

import java.util.List;

public interface ChatRoomQueryRepository {

    List<ChatRoomDocument> findAllRooms(int size, ChatRoomAllCursor cursor);
}
