package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ResponseCode {
    INVALID_CHAT_ROOM_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "CHAT-E-001", "채팅방 제목은 1자 이상 100자 이하여야 합니다."),
    INVALID_CHAT_ROOM_THUMBNAIL_LENGTH(HttpStatus.BAD_REQUEST, "CHAT-E-002", "채팅방 썸네일 오브젝트 키 길이가 너무 깁니다."),
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "CHAT-E-003", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT-E-004", "채팅방에 접근할 권한이 없습니다."),
    INVALID_CHAT_ROOM_CURSOR(HttpStatus.BAD_REQUEST, "CHAT-E-005", "유효하지 않은 채팅방 목록 커서입니다."),
    ALREADY_JOINED_CHAT_ROOM(HttpStatus.CONFLICT, "CHAT-E-006", "이미 참여 중인 채팅방입니다."),
    NOT_JOINED_CHAT_ROOM(HttpStatus.NOT_FOUND, "CHAT-E-007", "참여 중인 채팅방이 아닙니다."),
    OWNER_CANNOT_LEAVE_CHAT_ROOM(HttpStatus.BAD_REQUEST, "CHAT-E-008", "채팅방 생성자는 퇴장할 수 없습니다."),
    INVALID_CHAT_MESSAGE_LENGTH(HttpStatus.BAD_REQUEST, "CHAT-E-009", "채팅 메시지는 1자 이상 1000자 이하여야 합니다."),
    INVALID_CHAT_MESSAGE_IMAGE_KEY_LENGTH(HttpStatus.BAD_REQUEST, "CHAT-E-010", "채팅 이미지 오브젝트 키 길이가 너무 깁니다."),
    INVALID_CHAT_MESSAGE_PAYLOAD(HttpStatus.BAD_REQUEST, "CHAT-E-011", "텍스트 또는 이미지 중 하나만 포함해야 합니다."),
    INVALID_CHAT_MESSAGE_CURSOR(HttpStatus.BAD_REQUEST, "CHAT-E-012", "유효하지 않은 채팅 메시지 목록 커서입니다."),
    INVALID_CHAT_READ_STATE(HttpStatus.BAD_REQUEST, "CHAT-E-013", "유효하지 않은 읽음 상태 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
