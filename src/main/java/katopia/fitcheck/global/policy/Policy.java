package katopia.fitcheck.global.policy;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public final class Policy {

    // Member policy

    /**
     * 닉네임 허용 문자 정규식
     * - \p{L} : 모든 유니코드 문자 (한글, 영문 등)
     * - \p{N} : 모든 유니코드 숫자
     * - . _   : 점, 언더스코어 허용
     * - ^ $   : 전체 문자열이 해당 패턴과 정확히 일치해야 함
     */
    public static final String
            NICKNAME_REGEX = "^[\\p{L}\\p{N}._]+$",
            GENDER_M = "M", GENDER_F = "F";

    public static final int
            NICKNAME_MIN_LENGTH = 2, NICKNAME_MAX_LENGTH = 20,
            HEIGHT_MIN = 50, HEIGHT_MAX = 300,
            WEIGHT_MIN = 20, WEIGHT_MAX = 500,
            STYLE_MAX_COUNT = 2;

    // Post policy
    public static final int
            POST_CONTENT_MIN_LENGTH = 1, POST_CONTENT_MAX_LENGTH = 200,
            POST_IMAGE_MIN_COUNT = 1, POST_IMAGE_MAX_COUNT = 3,
            TAG_MIN_LENGTH = 1, TAG_MAX_LENGTH = 20,
            TAG_MAX_COUNT = 10;

    // Comment policy
    public static final int
            COMMENT_CONTENT_MIN_LENGTH = 1, COMMENT_CONTENT_MAX_LENGTH = 200;

    // Vote policy
    public static final int
            VOTE_TITLE_MIN_LENGTH = 1, VOTE_TITLE_MAX_LENGTH = 20,
            VOTE_IMAGE_MIN_COUNT = 1, VOTE_IMAGE_MAX_COUNT = 5;

    // Search policy
    public static final int
            SEARCH_MIN_QUERY_LENGTH = 2, SEARCH_MAX_QUERY_LENGTH = 100,
            SEARCH_MAX_FULLTEXT_QUERY_LENGTH = 200;

    // Pagination policy
    public static final int
            DEFAULT_PAGE_SIZE = 20;
    public static final String
            PAGE_VALUE = "size",
            CURSOR_VALUE = "after";

    // Upload policy
    public static final long PRESIGN_MAX_SIZE_BYTES = 30L * 1024 * 1024;

    public static final int IMAGE_OBJECT_KEY_MAX_LENGTH = 1024;
    public static final java.util.List<String> PRESIGN_ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "heic", "webp");

    // Auth policy
    public static final Duration JWT_ACCESS_TOKEN_TTL = Duration.ofMinutes(15);
    public static final Duration JWT_REFRESH_TOKEN_TTL = Duration.ofDays(14);
    public static final Duration JWT_REGISTRATION_TOKEN_TTL = Duration.ofMinutes(10);

    // descriptions
    public static final String NICKNAME_DES =
            "닉네임 (한글/영문/숫자/._, 최대 " + NICKNAME_MAX_LENGTH + "자)";
    public static final String GENDER_DES = "성별 (M/F)";
    public static final String HEIGHT_DES =
            "키(cm) 숫자 문자열 (" + HEIGHT_MIN + "~" + HEIGHT_MAX + ")";
    public static final String WEIGHT_DES =
            "몸무게(kg) 숫자 문자열 (" + WEIGHT_MIN + "~" + WEIGHT_MAX + ")";
    public static final String TAG_LIST_DES =
            "태그 목록(최대 " + TAG_MAX_COUNT + "개)";
    public static final String PRESIGN_CATEGORY_DES = "업로드 유형 (PROFILE/POST/VOTE)";
    public static final String PRESIGN_EXTENSIONS_DES = "확장자 목록: jpg | jpeg | png | heic | webp";
    public static final String PRESIGN_EXTENSION_DES = "확장자";

    public static final String
            FOLLOW_MESSAGE = "%s님이 팔로우했습니다.",
            POST_LIKE_MESSAGE = "%s님이 게시글을 좋아합니다.",
            POST_COMMENT_MESSAGE = "%s님이 댓글을 남겼습니다.",
            VOTE_CLOSED_MESSAGE = "투표가 종료되었습니다.",
            CHAT_MESSAGE = "%s: %s";

    public static final String
            NOTIFICATION_TYPE_FOLLOW = "FOLLOW",
            NOTIFICATION_TYPE_POST_LIKE = "POST_LIKE",
            NOTIFICATION_TYPE_POST_COMMENT = "POST_COMMENT",
            NOTIFICATION_TYPE_VOTE_CLOSED = "VOTE_CLOSED";

    private Policy() {
    }
}
