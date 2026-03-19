package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatMessageDocument;
import katopia.fitcheck.chat.domain.ChatMemberDocument;

import java.util.List;
import java.util.Map;

public interface ChatMessageQueryRepository {

    List<ChatMessageDocument> findMessages(String roomId, int size, Long afterMessageId);

    Map<String, Long> countUnreadMessagesByRoom(List<ChatMemberDocument> memberships);
}
