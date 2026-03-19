package katopia.fitcheck.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "chat_rooms")
public class ChatRoomDocument {

    @Id
    private String id;

    @Indexed(name = "idx_chat_rooms_owner")
    private Long ownerId;

    private String title;

    private int participantCount;

    private String thumbnailImageObjectKey;

    private Instant createdAt;

    private Instant updatedAt;

    public static ChatRoomDocument create(Long ownerId, String title, String thumbnailImageObjectKey) {
        Instant now = Instant.now();
        return ChatRoomDocument.builder()
                .ownerId(ownerId)
                .title(title)
                .participantCount(1)
                .thumbnailImageObjectKey(thumbnailImageObjectKey)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void incrementParticipantCount() {
        this.participantCount += 1;
        this.updatedAt = Instant.now();
    }

    public void decrementParticipantCount() {
        if (this.participantCount > 0) {
            this.participantCount -= 1;
        }
        this.updatedAt = Instant.now();
    }

    public void update(String title, String thumbnailImageObjectKey) {
        this.title = title;
        this.thumbnailImageObjectKey = thumbnailImageObjectKey;
        this.updatedAt = Instant.now();
    }
}
