package katopia.fitcheck.chat.domain;

import katopia.fitcheck.domain.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "chat_messages")
@CompoundIndexes({
        @CompoundIndex(name = "uidx_chat_messages_message_id", def = "{'messageId': 1}", unique = true),
        @CompoundIndex(name = "idx_chat_messages_room_message", def = "{'roomId': 1, 'messageId': -1}")
})
public class ChatMessageDocument {

    @Id
    private String id;

    private String roomId;

    private Long messageId;

    private Long senderId;

    private String senderNicknameSnapshot;

    private String senderProfileImageObjectKeySnapshot;

    private String message;

    private String imageObjectKey;

    private ChatMessageType messageType;

    private Instant createdAt;

    public static ChatMessageDocument create(
            String roomId,
            Long messageId,
            Member member,
            String message,
            String imageObjectKey
    ) {
        return message != null
                ? text(
                roomId,
                messageId, member.getId(), member.getNickname(),
                member.getProfileImageObjectKey(),
                message
        )
                : image(
                roomId,
                messageId,
                member.getId(),
                member.getNickname(),
                member.getProfileImageObjectKey(),
                imageObjectKey
        );
    }

    public static ChatMessageDocument text(
            String roomId,
            Long messageId,
            Long senderId,
            String senderNicknameSnapshot,
            String senderProfileImageObjectKeySnapshot,
            String message
    ) {
        return ChatMessageDocument.builder()
                .roomId(roomId)
                .messageId(messageId)
                .senderId(senderId)
                .senderNicknameSnapshot(senderNicknameSnapshot)
                .senderProfileImageObjectKeySnapshot(senderProfileImageObjectKeySnapshot)
                .message(message)
                .imageObjectKey(null)
                .messageType(ChatMessageType.TEXT)
                .createdAt(Instant.now())
                .build();
    }

    public static ChatMessageDocument image(
            String roomId,
            Long messageId,
            Long senderId,
            String senderNicknameSnapshot,
            String senderProfileImageObjectKeySnapshot,
            String imageObjectKey
    ) {
        return ChatMessageDocument.builder()
                .roomId(roomId)
                .messageId(messageId)
                .senderId(senderId)
                .senderNicknameSnapshot(senderNicknameSnapshot)
                .senderProfileImageObjectKeySnapshot(senderProfileImageObjectKeySnapshot)
                .message("사진")
                .imageObjectKey(imageObjectKey)
                .messageType(ChatMessageType.IMAGE)
                .createdAt(Instant.now())
                .build();
    }
}
