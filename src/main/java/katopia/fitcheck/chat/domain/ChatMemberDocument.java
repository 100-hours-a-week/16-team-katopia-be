package katopia.fitcheck.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "chat_members")
@CompoundIndexes({
        @CompoundIndex(name = "uidx_chat_members_room_member", def = "{'roomId': 1, 'memberId': 1}", unique = true),
        @CompoundIndex(name = "idx_chat_members_room_last_read", def = "{'roomId': 1, 'lastReadMessageId': 1}")
})
public class ChatMemberDocument {

    @Id
    private String id;

    private String roomId;

    @Indexed(name = "idx_chat_members_member")
    private Long memberId;

    private Instant joinedAt;

    private boolean realtimeNotificationEnabled;

    private Long lastReadMessageId;

    public static ChatMemberDocument join(String roomId, Long memberId) {
        return ChatMemberDocument.builder()
                .roomId(roomId)
                .memberId(memberId)
                .joinedAt(Instant.now())
                .realtimeNotificationEnabled(true)
                .lastReadMessageId(null)
                .build();
    }

    public void markLastReadMessageId(Long lastReadMessageId) {
        if (lastReadMessageId == null) {
            return;
        }
        if (this.lastReadMessageId == null || this.lastReadMessageId < lastReadMessageId) {
            this.lastReadMessageId = lastReadMessageId;
        }
    }
}
