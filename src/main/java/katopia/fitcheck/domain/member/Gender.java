package katopia.fitcheck.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    M("M"),
    F("F");

    private final String code;
}
