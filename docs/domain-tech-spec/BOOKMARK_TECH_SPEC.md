# 북마크 도메인 테크 스펙 (Bookmark)

## 배경 (Background)

- 프로젝트 목표: 게시글 북마크와 목록을 제공한다.
- 문제 정의:
  - 중복 북마크 방지와 빠른 목록 조회가 필요하다.

## 목표가 아닌 것 (Non-goals)

- 폴더/컬렉션 단위 북마크
- 공개/비공개 정책

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 게시글 대상만 지원
- 중복 북마크 금지

### 데이터 스키마 (ERD/DDL)

```sql
CREATE TABLE post_bookmarks (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uidx_post_bookmarks_member_post (member_id, post_id),
  KEY idx_post_bookmarks_member (member_id, created_at),
  KEY idx_post_bookmarks_post (post_id)
);
```

## API 명세 (API Specifications)

- 북마크: `POST /api/posts/{id}/bookmarks`
- 북마크 해제: `DELETE /api/posts/{id}/bookmarks`
- 내 북마크 목록: `GET /api/members/me/bookmarks` (요약 목록, 인피니티 스크롤)

## 장애/예외 처리

- 중복 북마크는 409
- 관계 없음은 404
 - 작성자 상태와 무관하게 노출

## 용어 정의 (Glossary)

- 북마크: 사용자별 게시글 저장
