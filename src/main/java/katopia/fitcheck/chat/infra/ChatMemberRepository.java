package katopia.fitcheck.chat.infra;

import katopia.fitcheck.chat.domain.ChatMemberDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends MongoRepository<ChatMemberDocument, String> {

    boolean existsByRoomIdAndMemberId(String roomId, Long memberId);

    Optional<ChatMemberDocument> findByRoomIdAndMemberId(String roomId, Long memberId);

    List<ChatMemberDocument> findAllByRoomIdOrderByJoinedAtAsc(String roomId);

    List<ChatMemberDocument> findAllByMemberIdAndRoomIdIn(Long memberId, Collection<String> roomIds);

    long deleteAllByRoomId(String roomId);
}
