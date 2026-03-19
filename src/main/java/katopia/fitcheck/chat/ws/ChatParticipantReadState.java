package katopia.fitcheck.chat.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.chat.domain.ChatMemberDocument;

public record ChatParticipantReadState(
        @Schema(description = "참여자 회원 ID", example = "2")
        Long memberId,

        @Schema(description = "참여자의 마지막 읽음 메시지 ID(null이면 아직 읽지 않음)", example = "12", nullable = true)
        Long lastReadMessageId
) {

    public static ChatParticipantReadState from(ChatMemberDocument memberDocument) {
        return new ChatParticipantReadState(
                memberDocument.getMemberId(),
                memberDocument.getLastReadMessageId()
        );
    }
}
