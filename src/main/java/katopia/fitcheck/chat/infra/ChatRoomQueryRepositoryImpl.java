package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatRoomDocument;
import katopia.fitcheck.chat.service.room.ChatRoomQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatRoomDocument> findAllRooms(int size, ChatRoomQueryService.ChatRoomAllCursor cursor) {
        Query query = new Query()
                .with(Sort.by(
                        Sort.Order.desc("updatedAt"),
                        Sort.Order.desc("_id")
                ))
                .with(PageRequest.of(0, size));

        if (cursor != null) {
            Criteria olderUpdatedAt = Criteria.where("updatedAt").lt(cursor.updatedAt());
            Criteria sameUpdatedAtLowerId = new Criteria().andOperator(
                    Criteria.where("updatedAt").is(cursor.updatedAt()),
                    Criteria.where("_id").lt(cursor.roomId())
            );
            query.addCriteria(new Criteria().orOperator(olderUpdatedAt, sameUpdatedAtLowerId));
        }

        return mongoTemplate.find(query, ChatRoomDocument.class);
    }
}
