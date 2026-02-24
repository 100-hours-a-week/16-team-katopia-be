package katopia.fitcheck.global.pagination;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public final class CursorPagingHelper {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final DateTimeFormatter CURSOR_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private CursorPagingHelper() {
    }

    public static int resolvePageSize(String sizeValue) {
        if (sizeValue == null) {
            return DEFAULT_PAGE_SIZE;
        }
        try {
            int parsed = Integer.parseInt(sizeValue);
            if (parsed <= 0) {
                throw new NumberFormatException("size must be positive");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new BusinessException(CommonErrorCode.INVALID_PAGE_SIZE_FORMAT);
        }
    }

    public static Cursor decodeCursor(String cursorValue) {
        try {
            String[] parts = cursorValue.split("\\|");
            if (parts.length != 2) {
                throw new IllegalArgumentException("invalid cursor");
            }
            LocalDateTime createdAt = LocalDateTime.parse(parts[0], CURSOR_FORMATTER);
            Long id = Long.parseLong(parts[1]);
            return new Cursor(createdAt, id);
        } catch (Exception ex) {
            throw new BusinessException(CommonErrorCode.INVALID_ID_FORMAT);
        }
    }

    public static String encodeCursor(LocalDateTime createdAt, Long id) {
        return String.format("%s|%d", CURSOR_FORMATTER.format(createdAt), id);
    }

    public static <T> String resolveNextCursor(
            List<T> items,
            int size,
            Function<T, LocalDateTime> createdAtResolver,
            Function<T, Long> idResolver
    ) {
        if (items == null || items.isEmpty() || items.size() < size) {
            return null;
        }
        T last = items.get(items.size() - 1);
        return encodeCursor(createdAtResolver.apply(last), idResolver.apply(last));
    }

    public record Cursor(LocalDateTime createdAt, Long id) {
    }
}
