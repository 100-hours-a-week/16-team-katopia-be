package katopia.fitcheck.member.domain;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.exception.code.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class MemberProfileValidator {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\p{L}\\p{N}._]+$");
    private static final int MAX_NICKNAME_LENGTH = 20;
    private static final int MIN_HEIGHT = 50;
    private static final int MAX_HEIGHT = 300;
    private static final int MIN_WEIGHT = 20;
    private static final int MAX_WEIGHT = 500;
    private static final int MAX_STYLE_COUNT = 2;

    public String normalizeNickname(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new BusinessException(requiredValue("닉네임"));
        }
        String trimmed = nickname.trim();
        if (trimmed.length() > MAX_NICKNAME_LENGTH) {
            throw new BusinessException(MemberErrorCode.INVALID_NICKNAME_LEN);
        }
        if (!NICKNAME_PATTERN.matcher(trimmed).matches()) {
            throw new BusinessException(MemberErrorCode.INVALID_NICKNAME_CHARACTERS);
        }
        return trimmed;
    }

    public Gender parseGender(String genderValue) {
        if (!StringUtils.hasText(genderValue)) {
            return null;
        }
        try {
            return Gender.valueOf(genderValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(MemberErrorCode.INVALID_GENDER_FORMAT);
        }
    }

    public Short parseHeight(String height) {
        if (!StringUtils.hasText(height)) {
            return null;
        }
        int parsed = parseBodyMeasurement(height);
        if (parsed < MIN_HEIGHT || parsed > MAX_HEIGHT) {
            throw new BusinessException(MemberErrorCode.INVALID_BODY_RANGE);
        }
        return (short) parsed;
    }

    public Short parseWeight(String weight) {
        if (!StringUtils.hasText(weight)) {
            return null;
        }
        int parsed = parseBodyMeasurement(weight);
        if (parsed < MIN_WEIGHT || parsed > MAX_WEIGHT) {
            throw new BusinessException(MemberErrorCode.INVALID_BODY_RANGE);
        }
        return (short) parsed;
    }

    public boolean validateNotificationFlag(Boolean value) {
        if (value == null) {
            throw new BusinessException(MemberErrorCode.INVALID_NOTIFICATION_FORMAT);
        }
        return value;
    }

    public Set<StyleType> parseStyles(List<String> styles) {
        if (styles == null) {
            return null;
        }
        if (styles.isEmpty()) {
            return Collections.emptySet();
        }

        EnumSet<StyleType> parsed = EnumSet.noneOf(StyleType.class);
        for (String style : styles) {
            if (!StringUtils.hasText(style)) {
                throw new BusinessException(MemberErrorCode.INVALID_STYLE_FORMAT);
            }
            try {
                parsed.add(StyleType.valueOf(style.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                throw new BusinessException(MemberErrorCode.INVALID_STYLE_FORMAT);
            }
        }
        if (parsed.size() > MAX_STYLE_COUNT) {
            throw new BusinessException(MemberErrorCode.STYLE_LIMIT_EXCEEDED);
        }
        return parsed;
    }

    private int parseBodyMeasurement(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException(MemberErrorCode.INVALID_BODY_FORMAT);
        }
    }

    private ResponseCode requiredValue(String field) {
        return new ResponseCode() {
            @Override
            public HttpStatus getStatus() {
                return CommonErrorCode.REQUIRED_VALUE.getStatus();
            }

            @Override
            public String getMessage() {
                return String.format(CommonErrorCode.REQUIRED_VALUE.getMessage(), field);
            }

            @Override
            public String getCode() {
                return CommonErrorCode.REQUIRED_VALUE.getCode();
            }
        };
    }
}
