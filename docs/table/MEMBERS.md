# Members

## 1) 테이블 설명
사용자 계정 및 프로필의 기준 엔티티.  
카카오 소셜 로그인 기반 사용자 식별, 프로필 정보, 상태(PENDING/ACTIVE/WITHDRAWN) 관리에 사용된다.

## 2) 관계
- members(1) : posts/comments/post_likes/post_tags/post_comments (N)
- members(1) : refresh_tokens (N)
- member_styles는 members의 ElementCollection (1:N)

## 3) 설계 근거
- 대리키(id)를 사용해 다른 도메인에서 안정적으로 참조한다.
- oauth2_provider + oauth2_user_id 조합으로 소셜 계정을 유니크하게 보장한다.
- 탈퇴 시 soft delete + 익명화를 적용해 연관 데이터 무결성을 유지한다.

## 4) 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 사용자 식별자
2 | email | VARCHAR(320) |  | 이메일(보조 식별자)
3 | nickname | VARCHAR(20) | unique, nn | 닉네임
4 | oauth2_provider | VARCHAR(20) | nn | 소셜 제공자
5 | oauth2_user_id | VARCHAR | nn | 소셜 사용자 ID(문자열)
6 | profile_image_object_key | VARCHAR(1024) |  | 프로필 이미지 오브젝트 키
7 | gender | CHAR(1) |  | 성별(M/F)
8 | height | SMALLINT |  | 키(cm)
9 | weight | SMALLINT |  | 몸무게(kg)
10 | enable_realtime_notification | BOOLEAN | nn | 실시간 알림 허용 여부
11 | created_at | DATETIME | nn | 생성일
12 | updated_at | DATETIME | nn | 수정일
13 | deleted_at | DATETIME |  | 탈퇴일
14 | terms_agreed_at | DATETIME |  | 약관 동의일
15 | account_status | VARCHAR(10) | nn | 상태(PENDING/ACTIVE/WITHDRAWN)

## 5) 인덱스/제약
- `uk_members_nickname (nickname)`
- `uk_members_oauth2_user (oauth2_provider, oauth2_user_id)`

## 6) 운영 정책
- 탈퇴 시 nickname 익명화 + deleted_at 기록
- WITHDRAWN 상태는 14일 유예 후 재가입 허용
- 탈퇴 시 profile_image_object_key, styles, 알림 설정은 초기화됨
- 공개 프로필 조회에서 상태별(PENDING/WITHDRAWN) 메시지 분기
