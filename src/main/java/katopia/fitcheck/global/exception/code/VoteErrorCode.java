package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VoteErrorCode implements ResponseCode {
    TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "VOTE-E-000", "제목은 필수 입력 항목입니다."),
    TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "VOTE-E-001", "제목은 최대 20자 입니다."),
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "VOTE-E-002", "투표를 찾을 수 없습니다."),
    VOTE_CLOSED(HttpStatus.CONFLICT, "VOTE-E-003", "종료된 투표입니다."),
    ALREADY_PARTICIPATED(HttpStatus.CONFLICT, "VOTE-E-004", "이미 참여한 투표입니다."),
    SELF_PARTICIPATION_NOT_ALLOWED(HttpStatus.CONFLICT, "VOTE-E-005", "본인 투표에는 참여할 수 없습니다."),

    IMAGE_COUNT_INVALID(HttpStatus.BAD_REQUEST, "VOTE-E-010", "이미지는 최소 1장 이상, 최대 5장까지 등록 가능합니다."),

    VOTE_ITEM_REQUIRED(HttpStatus.BAD_REQUEST, "VOTE-E-020", "투표 항목은 최소 1개 이상 선택해야 합니다."),
    VOTE_ITEM_DUPLICATED(HttpStatus.BAD_REQUEST, "VOTE-E-021", "중복된 투표 항목이 포함되어 있습니다."),
    VOTE_ITEM_INVALID(HttpStatus.BAD_REQUEST, "VOTE-E-022", "유효하지 않은 투표 항목입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
