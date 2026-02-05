# Posts

## 1) 테이블 설명
게시글 본문과 집계(좋아요/댓글) 정보를 저장하는 테이블.

## 2) 관계
- members(1) : posts(N)
- posts(1) : post_images(N)
- posts(1) : post_tags(N)
- posts(1) : post_likes(N)
- posts(1) : post_comments(N)

## 3) 설계 근거
- 작성자 기준 조회가 빈번해 `member_id` 기반 인덱스를 둔다.
- like/comment 집계는 동시성 고려해 별도 컬럼으로 관리한다.

## 4) 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 게시글 ID
2 | member_id | BIGINT | fk, nn | 작성자 ID
3 | content | VARCHAR(200) | nn | 본문
4 | like_count | BIGINT | nn, default 0 | 좋아요 수
5 | comment_count | BIGINT | nn, default 0 | 댓글 수
6 | created_at | DATETIME | nn | 작성일
7 | updated_at | DATETIME | nn | 수정일

## 5) 인덱스/제약
- `idx_posts_member_created (member_id, created_at)`
- `idx_posts_created (created_at)`

## 6) 운영 정책
- 게시글 삭제 시 댓글/좋아요/태그는 별도 정리 로직으로 삭제
- 좋아요/댓글 수는 동시성 대응을 위해 DB 업데이트 쿼리로 증감
