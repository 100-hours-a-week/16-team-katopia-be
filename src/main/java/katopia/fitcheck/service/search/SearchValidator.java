package katopia.fitcheck.service.search;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SearchValidator {

    private static final int MIN_QUERY_LENGTH = 2;
    private static final int MAX_QUERY_LENGTH = 100;

    public String requireQuery(String query) {
        return validateQuery(query, "query");
    }

    public String requireQuery(String query, int maxLength) {
        return validateQuery(query, "query", maxLength);
    }

    public String validateQuery(String query, String fieldName) {
        return validateQuery(query, fieldName, MAX_QUERY_LENGTH);
    }

    public String validateQuery(String query, String fieldName, int maxLength) {
        if (!StringUtils.hasText(query)) {
            throw new BusinessException(requiredValue(fieldName));
        }
        String trimmed = query.trim();
        if (trimmed.length() < MIN_QUERY_LENGTH || trimmed.length() > maxLength) {
            throw new BusinessException(CommonErrorCode.INVALID_SEARCH_QUERY_LEN);
        }
        return trimmed;
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
