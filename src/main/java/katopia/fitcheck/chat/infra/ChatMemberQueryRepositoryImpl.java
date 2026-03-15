package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatMemberDocument;
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
public class ChatMemberQueryRepositoryImpl implements ChatMemberQueryRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatMemberDocument> findJoinedRooms(Long memberId, int size, ChatRoomQueryService.ChatRoomCursor cursor) {
        Query query = new Query()
                .addCriteria(Criteria.where("memberId").is(memberId))
                .with(Sort.by(
                        Sort.Order.desc("joinedAt"),
                        Sort.Order.desc("_id")
                ))
                .with(PageRequest.of(0, size));

        if (cursor != null) {
            Criteria olderJoinedAt = Criteria.where("joinedAt").lt(cursor.joinedAt());
            Criteria sameJoinedAtLowerId = new Criteria().andOperator(
                    Criteria.where("joinedAt").is(cursor.joinedAt()),
                    Criteria.where("_id").lt(cursor.memberDocumentId())
            );
            query.addCriteria(new Criteria().orOperator(olderJoinedAt, sameJoinedAtLowerId));
        }

        return mongoTemplate.find(query, ChatMemberDocument.class);
    }
}
