# 회원/프로필 도메인 테크 스펙 (Member/Profile)

## 배경 (Background)

- 프로젝트 목표: 가입 이후 프로필 정보를 일관된 정책으로 관리한다.
- 문제 정의:
  - 닉네임/스타일/신체 정보 검증 기준이 분산되면 정책 불일치가 발생한다.
  - 선택 필드 업데이트 시 null 처리 규칙이 필요하다.
  - 탈퇴 계정의 노출 정책을 명확히 해야 한다.
- 가설:
  - 입력 정규화와 검증을 DTO 단계에 통일하면 정책 일관성이 유지된다.

## 목표가 아닌 것 (Non-goals)

- 이메일/비밀번호 회원가입
- 관리자용 회원 관리 기능
- 팔로우 차단/뮤트

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 닉네임
  - 길이: 2~20자
  - 허용 문자: 영문/숫자/한글/.(점)/_(언더스코어), 공백 불가
  - 중복 시 409
- 프로필 이미지
  - URL이 아닌 `profile_image_object_key` 사용
- 스타일
  - 최대 2개
- 신체 정보
  - 키: 50~300
  - 몸무게: 20~500
- 탈퇴 처리
  - `account_status=WITHDRAWN`, `deleted_at` 기록
  - nickname 익명화, 이미지/스타일/알림 초기화

### 입력 처리 규칙

- `MemberProfileInputResolver`로 null/빈 문자열 정규화
- update 요청에서 null 필드는 기존 값 유지
- signup 요청에서 선택 필드는 null 유지

## 데이터베이스 스키마 (ERD/DDL)

```sql
CREATE TABLE members (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(320) NULL,
  nickname VARCHAR(20) NOT NULL,
  oauth2_provider VARCHAR(20) NOT NULL,
  oauth2_user_id VARCHAR(255) NOT NULL,
  profile_image_object_key VARCHAR(1024) NULL,
  gender ENUM('M','F') NULL,
  height SMALLINT NULL,
  weight SMALLINT NULL,
  enable_realtime_notification BOOLEAN NOT NULL,
  post_count BIGINT NOT NULL DEFAULT 0,
  following_count BIGINT NOT NULL DEFAULT 0,
  follower_count BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted_at DATETIME NULL,
  terms_agreed_at DATETIME NULL,
  account_status ENUM('PENDING','ACTIVE','WITHDRAWN') NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_members_nickname (nickname),
  UNIQUE KEY uk_members_oauth2_user (oauth2_provider, oauth2_user_id)
);

CREATE TABLE member_styles (
  member_id BIGINT NOT NULL,
  style VARCHAR(30) NOT NULL
);
```

## API 명세 (API Specifications)

- 닉네임 유효성/중복 확인: `GET /api/members/check?nickname=...`
- 공개 프로필 조회: `GET /api/members/{memberId}` (팔로우 여부 포함 예정)
- 공개 사용자 게시글 목록: `GET /api/members/{memberId}/posts`
- 내 프로필 조회: `GET /api/members/me`
- 내 프로필 수정: `PATCH /api/members`
- 회원 탈퇴: `DELETE /api/members`

## 트랜잭션/정합성 정책

- 닉네임 중복은 DB 유니크로 최종 보장
- 탈퇴는 하드 삭제가 아닌 상태 전환

## 장애/예외 처리

- 탈퇴 계정의 접근은 404로 통일
- 유효성 오류는 400으로 통일

## 보안 고려사항

- 프로필 조회는 공개/인증 경로를 구분
- 내 프로필 조회는 인증 필요

## 오류 코드 요약 (Member 영역)

- MEMBER-E-001~004: 닉네임 형식/중복
- MEMBER-E-020~024: 성별/키/몸무게 형식 및 범위
- MEMBER-E-030~031: 스타일 형식/개수 제한
- MEMBER-E-040: 알림 설정 값 오류
- MEMBER-E-050~052: 존재하지 않는/미가입/탈퇴 회원

## 변경 사항

- 프로필 이미지 필드를 URL → objectKey로 통일
- 검증을 DTO 단계(@Valid)로 이동
- 프로필/검색 응답에 `isFollowing` 표시 추가 예정

## 용어 정의 (Glossary)

- PENDING: 소셜 로그인 완료, 회원가입 미완료
- ACTIVE: 회원가입 완료
- WITHDRAWN: 탈퇴 상태
