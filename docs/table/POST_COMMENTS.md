# Post Comments

## 1) 테이블 설명
게시글 댓글 테이블.

## 2) 관계
- posts(1) : post_comments(N)
- members(1) : post_comments(N)

## 3) 설계 근거
- 댓글 단위 수정/삭제를 위해 대리키(id)를 사용한다.
- 게시글별 인피니티 스크롤을 위해 (post_id, created_at) 인덱스를 둔다.

## 4) 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 댓글 ID
2 | post_id | BIGINT | fk, nn | 게시글 ID
3 | member_id | BIGINT | fk, nn | 작성자 ID
4 | content | VARCHAR(200) | nn | 댓글 본문
5 | created_at | DATETIME | nn | 작성일
6 | updated_at | DATETIME | nn | 수정일

## 5) 인덱스/제약
- `idx_post_comments_post_created (post_id, created_at)`
- `idx_post_comments_member (member_id)`

## 6) 운영 정책
- 삭제 시 하드 삭제, 게시글 삭제 시 일괄 삭제
- 삭제 시 게시글의 comment_count를 감소
