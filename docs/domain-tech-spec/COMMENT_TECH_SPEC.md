# 댓글 도메인 테크 스펙 (Comment)

## 배경 (Background)

- 프로젝트 목표: 게시글 댓글을 안정적으로 제공한다.
- 문제 정의:
  - 댓글 목록은 무한 스크롤 기반이므로 커서 정책이 필요하다.
  - 댓글 작성 및 삭제 시 게시글 댓글 수 정합성이 필요하다.

## 목표가 아닌 것 (Non-goals)

- 대댓글/스레드 구조
- 멘션 연동

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 본문 길이: 1~200자
- 목록 페이징: `createdAt + id` 커서
- 삭제 정책: 소프트 삭제
- 댓글 수: 배치 동기화(현재 동기 증감 미적용)

### 데이터 스키마 (ERD/DDL)

```sql
CREATE TABLE post_comments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  content VARCHAR(200) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_post_comments_post_deleted_created (post_id, deleted_at, created_at),
  KEY idx_post_comments_member (member_id)
);
```

## API 명세 (API Specifications)

- 댓글 작성: `POST /api/posts/{postId}/comments`
- 댓글 목록: `GET /api/posts/{postId}/comments`
- 댓글 수정: `PATCH /api/posts/{postId}/comments/{id}`
- 댓글 삭제: `DELETE /api/posts/{postId}/comments/{id}`

## 트랜잭션/정합성 정책

- 생성/수정/삭제 단일 트랜잭션
- 삭제는 soft delete로 처리
- 댓글 수는 배치로 동기화(동기 증감 미적용)

## 장애/예외 처리

- 본문 검증 실패는 400
- 댓글 없음은 404
- 권한 실패는 403

## 용어 정의 (Glossary)

- 커서: createdAt + id 기반 다음 페이지 토큰
