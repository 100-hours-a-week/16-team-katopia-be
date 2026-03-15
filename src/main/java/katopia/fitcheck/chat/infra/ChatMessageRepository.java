package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatMessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDocument, String>, ChatMessageQueryRepository {

    boolean existsByRoomIdAndMessageId(String roomId, Long messageId);

    long deleteAllByRoomId(String roomId);
}
