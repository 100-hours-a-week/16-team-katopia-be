package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatRoomDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoomDocument, String> {
}
