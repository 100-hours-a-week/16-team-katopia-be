package katopia.fitcheck.domain.member;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class MemberProfileValidator {

    public Gender parseGender(String genderValue) {
        if (!StringUtils.hasText(genderValue)) {
            return null;
        }
        return Gender.valueOf(genderValue.trim().toUpperCase(Locale.ROOT));
    }

    public Short parseHeight(String height) {
        if (!StringUtils.hasText(height)) {
            return null;
        }
        return Short.valueOf(height.trim());
    }

    public Short parseWeight(String weight) {
        if (!StringUtils.hasText(weight)) {
            return null;
        }
        return Short.valueOf(weight.trim());
    }

    public Set<StyleType> parseStyles(List<String> styles) {
        if (styles == null) {
            return null;
        }
        if (styles.isEmpty()) {
            return Collections.emptySet();
        }

        Set<StyleType> parsed = Set.copyOf(styles.stream()
                .map(style -> StyleType.valueOf(style.trim().toUpperCase(Locale.ROOT)))
                .toList());
        return parsed;
    }
}
