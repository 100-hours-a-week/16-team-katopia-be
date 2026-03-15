package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatMessageDocument;
import katopia.fitcheck.chat.domain.ChatMemberDocument;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ChatMessageQueryRepositoryImpl implements ChatMessageQueryRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatMessageDocument> findMessages(String roomId, int size, Long afterMessageId) {
        Query query = new Query()
                .addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Order.desc("messageId")))
                .with(PageRequest.of(0, size));

        if (afterMessageId != null) {
            query.addCriteria(Criteria.where("messageId").lt(afterMessageId));
        }

        return mongoTemplate.find(query, ChatMessageDocument.class);
    }

    @Override
    public Map<String, Long> countUnreadMessagesByRoom(List<ChatMemberDocument> memberships) {
        if (memberships == null || memberships.isEmpty()) {
            return Map.of();
        }

        List<Criteria> unreadConditions = new ArrayList<>(memberships.size());
        for (ChatMemberDocument membership : memberships) {
            String roomId = membership.getRoomId();
            Long lastReadMessageId = membership.getLastReadMessageId();

            if (lastReadMessageId == null) {
                unreadConditions.add(Criteria.where("roomId").is(roomId));
                continue;
            }

            unreadConditions.add(new Criteria().andOperator(
                    Criteria.where("roomId").is(roomId),
                    Criteria.where("messageId").gt(lastReadMessageId)
            ));
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().orOperator(unreadConditions.toArray(Criteria[]::new))),
                Aggregation.group("roomId").count().as("unreadCount")
        );

        List<Document> results = mongoTemplate.aggregate(aggregation, "chat_messages", Document.class).getMappedResults();
        Map<String, Long> unreadCounts = new LinkedHashMap<>(results.size());
        for (Document document : results) {
            Number unreadCount = document.get("unreadCount", Number.class);
            unreadCounts.put(document.getString("_id"), unreadCount != null ? unreadCount.longValue() : 0L);
        }
        return unreadCounts;
    }
}
