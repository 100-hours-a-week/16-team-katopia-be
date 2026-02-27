package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VoteSuccessCode implements ResponseCode {
    VOTE_CREATED(HttpStatus.CREATED, "VOTE-S-001", "투표가 생성되었습니다."),
    VOTE_LISTED(HttpStatus.OK, "VOTE-S-002", "투표 목록 조회에 성공했습니다."),
    VOTE_CANDIDATE_FETCHED(HttpStatus.OK, "VOTE-S-003", "참여 가능한 투표를 조회했습니다."),
    VOTE_FETCHED(HttpStatus.OK, "VOTE-S-004", "투표 조회에 성공했습니다."),
    VOTE_PARTICIPATED(HttpStatus.CREATED, "VOTE-S-005", "투표 참여가 완료되었습니다."),
    VOTE_DELETED(HttpStatus.NO_CONTENT, "VOTE-S-006", "투표가 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
