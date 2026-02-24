# 게시글 도메인 테크 스펙 (Post)

## 배경 (Background)

- 프로젝트 목표: 이미지 기반 게시글을 안정적으로 생성/조회/수정/삭제한다.
- 문제 정의:
  - 이미지/태그 컬렉션의 순서/중복 정책이 필요하다.
  - 댓글 수는 동시성에 취약하므로 DB 단위 증감이 필요하다.
- 가설:
  - 이미지/태그 정책을 고정하면 일관된 UX를 보장할 수 있다.

## 목표가 아닌 것 (Non-goals)

- 게시글 이미지 교체

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 이미지
  - objectKey만 저장
  - 최소 1장, 최대 3장
  - sort_order로 순서 관리
- 태그
  - `#` 접두사 제거 후 저장
  - 길이 1~20, 최대 10개
- 수정 범위
  - 본문/태그만 수정, 이미지 변경 불가
- 좋아요/댓글 수
  - DB update 쿼리로 증감

### 데이터 스키마 (ERD/DDL)

```sql
CREATE TABLE posts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  content VARCHAR(200) NOT NULL,
  like_count BIGINT NOT NULL DEFAULT 0,
  comment_count BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_posts_member_created (member_id, created_at),
  KEY idx_posts_created (created_at)
);

CREATE TABLE post_images (
  post_id BIGINT NOT NULL,
  sort_order INT NOT NULL,
  image_object_key VARCHAR(1024) NOT NULL,
  UNIQUE KEY uk_post_images_post_order (post_id, sort_order),
  KEY idx_post_images_order (post_id, sort_order)
);

CREATE TABLE tags (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(20) NOT NULL,
  UNIQUE KEY uk_tags_name (name),
  KEY idx_tags_name (name)
);

CREATE TABLE post_tags (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  UNIQUE KEY uidx_post_tags_post_tag (post_id, tag_id),
  KEY idx_post_tags_tag_post (tag_id, post_id)
);

CREATE TABLE post_likes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  UNIQUE KEY uidx_post_likes_member_post (member_id, post_id),
  KEY idx_post_likes (member_id, post_id)
);
```

## API 명세 (API Specifications)

- 게시글 작성: `POST /api/posts`
- 게시글 목록: `GET /api/posts`
- 게시글 상세: `GET /api/posts/{id}`
- 홈 피드(팔로우+내 게시글): `GET /api/home/posts`
- 게시글 수정: `PATCH /api/posts/{id}`
- 게시글 삭제: `DELETE /api/posts/{id}`
- 좋아요: `POST /api/posts/{id}/likes`
- 좋아요 취소: `DELETE /api/posts/{id}/likes`

## 트랜잭션/정합성 정책

- 생성/수정은 단일 트랜잭션
- 삭제 시 댓글/좋아요/태그 관계를 선삭제

## 장애/예외 처리

- 본문/태그/이미지 유효성 실패는 400
- 대상 게시글 없음은 404
- 권한 실패는 403

## 오류 코드 요약 (Post 영역)

- POST-E-000~001: 본문 필수/길이
- POST-E-002: 게시글 없음
- POST-E-010: 이미지 개수 제한
- POST-E-020~021: 태그 길이/개수 제한

## 변경 사항

- 이미지 변경 기능 제거
- 태그 저장 시 `#` 제거

## 용어 정의 (Glossary)

- objectKey: 업로드된 이미지의 S3 키
- sort_order: 이미지 표시 순서
