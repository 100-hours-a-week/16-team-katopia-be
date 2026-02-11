package katopia.fitcheck.global.pagination;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CursorPagingHelperTest {

    @Nested
    class ResolvePageSizeCases {

        @Test
        @DisplayName("TC-PAGE-S-01 size 미지정 시 기본값 반환")
        void tcPageS01_resolvePageSize_default() {
            int size = CursorPagingHelper.resolvePageSize(null);

            assertThat(size).isEqualTo(20);
        }

        @Test
        @DisplayName("TC-PAGE-S-02 size 파싱 성공")
        void tcPageS02_resolvePageSize_success() {
            int size = CursorPagingHelper.resolvePageSize("30");

            assertThat(size).isEqualTo(30);
        }

        @Test
        @DisplayName("TC-PAGE-F-01 size 파싱 실패")
        void tcPageF01_resolvePageSize_invalid() {
            assertThatThrownBy(() -> CursorPagingHelper.resolvePageSize("0"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(ex -> ((BusinessException) ex).getErrorCode())
                    .isEqualTo(CommonErrorCode.INVALID_PAGE_SIZE_FORMAT);
        }
    }

    @Nested
    class CursorFormatCases {

        @Test
        @DisplayName("TC-PAGE-S-03 cursor 인코딩/디코딩 성공")
        void tcPageS03_encodeDecodeCursor_success() {
            LocalDateTime now = LocalDateTime.of(2026, 2, 7, 12, 0);

            String encoded = CursorPagingHelper.encodeCursor(now, 10L);
            CursorPagingHelper.Cursor decoded = CursorPagingHelper.decodeCursor(encoded);

            assertThat(decoded.createdAt()).isEqualTo(now);
            assertThat(decoded.id()).isEqualTo(10L);
        }

        @Test
        @DisplayName("TC-PAGE-F-02 cursor 디코딩 실패")
        void tcPageF02_decodeCursor_invalid() {
            assertThatThrownBy(() -> CursorPagingHelper.decodeCursor("invalid"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(ex -> ((BusinessException) ex).getErrorCode())
                    .isEqualTo(CommonErrorCode.INVALID_ID_FORMAT);
        }
    }
}
