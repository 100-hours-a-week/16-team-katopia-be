package katopia.fitcheck.chat.service.message;

import katopia.fitcheck.chat.domain.ChatSequenceDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageSequenceService {

    private static final String CHAT_MESSAGE_SEQUENCE_KEY = "chat_messages";

    private final MongoTemplate mongoTemplate;

    public long nextMessageId() {
        Query query = new Query(Criteria.where("_id").is(CHAT_MESSAGE_SEQUENCE_KEY));
        Update update = new Update().inc("sequence", 1L);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

        ChatSequenceDocument sequence = mongoTemplate.findAndModify(
                query,
                update,
                options,
                ChatSequenceDocument.class
        );
        if (sequence == null) {
            throw new IllegalStateException("chat message sequence increment failed");
        }
        return sequence.getSequence();
    }
}
