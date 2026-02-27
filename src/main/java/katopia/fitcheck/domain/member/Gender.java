package katopia.fitcheck.domain.member;

import katopia.fitcheck.global.policy.Policy;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    M(Policy.GENDER_M),
    F(Policy.GENDER_F);

    private final String code;
}
