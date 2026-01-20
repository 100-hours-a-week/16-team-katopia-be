package katopia.fitcheck.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberSuccessCode implements ResponseCode {
    NICKNAME_AVAILABLE(HttpStatus.OK, "MEMBER-S-001", "사용 가능한 닉네임입니다."),
    PROFILE_UPDATED(HttpStatus.OK, "MEMBER-S-003", "회원 정보가 수정되었습니다."),
    PROFILE_FETCHED(HttpStatus.OK, "MEMBER-S-004", "프로필 조회가 성공적으로 완료되었습니다."),
    MEMBER_WITHDRAWN(HttpStatus.NO_CONTENT, "MEMBER-S-005", "회원 탈퇴가 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
