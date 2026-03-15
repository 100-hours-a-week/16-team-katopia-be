package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatSuccessCode implements ResponseCode {
    CHAT_ROOM_CREATED(HttpStatus.CREATED, "CHAT-S-001", "채팅방이 생성되었습니다."),
    CHAT_ROOM_LISTED(HttpStatus.OK, "CHAT-S-002", "채팅방 목록 조회에 성공했습니다."),
    CHAT_ROOM_JOINED(HttpStatus.OK, "CHAT-S-003", "채팅방 참여가 완료되었습니다."),
    CHAT_ROOM_LEFT(HttpStatus.OK, "CHAT-S-004", "채팅방 퇴장이 완료되었습니다."),
    CHAT_ROOM_UPDATED(HttpStatus.OK, "CHAT-S-007", "채팅방이 수정되었습니다."),
    CHAT_ROOM_DELETED(HttpStatus.NO_CONTENT, "CHAT-S-008", "채팅방이 삭제되었습니다."),
    CHAT_MESSAGE_CREATED(HttpStatus.CREATED, "CHAT-S-005", "채팅 메시지가 생성되었습니다."),
    CHAT_MESSAGE_LISTED(HttpStatus.OK, "CHAT-S-006", "채팅 메시지 목록 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
