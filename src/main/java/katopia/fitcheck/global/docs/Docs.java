package katopia.fitcheck.global.docs;

import katopia.fitcheck.global.policy.Policy;

public final class Docs {
    // Member
    public static final String
            MEMBER_SIGNUP_REQUEST_DES = "회원가입 요청",
            MEMBER_PROFILE_UPDATE_REQUEST_DES = "내 정보 수정 요청",
            EMAIL_DES = "이메일", EMAIL = "user@example.com",
            PROFILE_DES = "공개 정보",
            NICKNAME_DES = "사용자 닉네임",
            NICKNAME = "member_1",
            GENDER = "M",
            HEIGHT_DES = "키(cm)", HEIGHT = "172",
            WEIGHT_DES = "몸무게(kg)",
            WEIGHT = "68",
            NOTIFICATION_DES = "실시간 알림 허용 여부",
            NOTIFICATION = "true",
            STYLE_LIST_DES = "스타일 목록",
            STYLE_LIST = "[\"CASUAL\", \"MINIMAL\"]";

    // Follow
    public static final String
            FOLLOW_LIST_DES = "팔로워/팔로잉 목록",
            FOLLOW_STATUS = "팔로우 여부";


    // Aggregate
    public static final String
            AGGREGATE_DES = "집계 정보",
            POST_COUNT_DES = "게시글 수",
            COMMENT_COUNT_DES = "댓글 수",
            POST_LIKE_COUNT_DES = "게시글 좋아요 수",
            FOLLOWER_COUNT_DES = "팔로워 수", FOLLOWING_COUNT_DES = "팔로잉 수";


    // Auth
    public static final String
            AT_DES = "액세스 토큰입니다. 인증/인가가 필요한 요청은 Authorization 헤더에 Bearer {AT} 형식으로 전달합니다.",
            AT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

    // S3
    public static final String
            IMAGE_OBJECT_KEY_DES = "이미지 S3 오브젝트 키",
            IMAGE_OBJECT_KEY = "{domain}/{memberId}/uuid.{extension}",
            IMAGE_OBJECT_KEY_LIST_DES = "이미지 S3 오브젝트 키 목록",
            IMAGE_OBJECT_KEY_LIST = "[\""+IMAGE_OBJECT_KEY+"\"]";

    // Post/Comment
    public static final String
            POST_LIST_DES = "게시글 목록",
            POST_IMAGE_LIST_DES = "게시글 이미지 목록",
            POST_CONTENT_DES = "게시글 본문", POST_CONTENT = "오늘의 코디입니다.",
            POST_CONTENT_UPDATE_DES = "수정할 게시글 본문", POST_CONTENT_UPDATE = "수정된 코디 소개입니다.",
            COMMENT_CONTENT_LIST_DES = "댓글 목록입니다.",
            COMMENT_CONTENT_DES = "댓글 본문", COMMENT_CONTENT = "잘 어울려요~",
            TAG_LIST = "[\"DAILY\", \"MINIMAL\"]",
            TAG_DES = "태그";

    // Vote
    public static final String VOTE_TITLE_DES = "투표 제목",
            VOTE_TITLE = "오늘의 코디 투표",
            VOTE_IMAGE_OBJECT_KEY_LIST = "[\"votes/1/1700000000000-uuid.png\"]",
            VOTE_IMAGE_OBJECT_KEY_EXAMPLE = "votes/1/1700000000000-uuid.png",
            VOTE_ITEM_ID_DES = "투표 항목 ID",
            VOTE_ITEM_ID_LIST_DES = "투표 항목 ID 목록",
            VOTE_ITEM_ID_LIST_EXAMPLE = "[10, 11]";


    // Presign/Upload
    public static final String PRESIGN_CATEGORY_POST = "POST",
            PRESIGN_EXTENSIONS_EXAMPLE = "[\"jpg\",\"png\"]",
            PRESIGN_EXTENSION_EXAMPLE = "jpg";

    // Auth/Validation
    public static final String AT_MISSING_OR_INVALID_DES = "AT재발급 필요. AT 누락/만료/위조",
            RT_MISSING_OR_INVALID_DES = "재로그인 필요. RT 누락/만료/위조",
            INPUT_VALIDATION_DES = "입력값 검증/제한",
            ACCESS_DENIED_DES = "권한이 없습니다.",
            NOT_FOUND_DES = "요청한 리소스를 찾을 수 없습니다.";

    // Common
    public static final String ID_DES = "식별자 ID",
            PAGE_DES = "페이지 크기", PAGE = Policy.DEFAULT_PAGE_SIZE + "",
            CURSOR = "2026-01-01T12:34:56Z|1", CURSOR_DES = "커서 (createdAt|id)",
            CURSOR_PAGING_DES = "커서 기반 인피니티 스크롤을 지원합니다.",
            CREATED_AT_DES = "생성 시간",
            UPDATED_AT_DES = "수정 시간",
            TIMESTAMP = "2026-01-01T12:34:56Z",
            AUTHOR_DES = "작성자",
            ORDER_DES = "순서";


    private Docs() {
    }
}
