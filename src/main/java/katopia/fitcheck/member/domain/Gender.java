package katopia.fitcheck.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    M("M"),
    F("F");

    private final String code;
}
