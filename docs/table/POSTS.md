# Posts

## 1. 테이블 설명
게시글 본문과 집계(좋아요/댓글) 정보를 저장하는 테이블.

### 연관 관계
- members(1) : posts(N)
- posts(1) : post_images(N)
- posts(1) : post_tags(N)
- posts(1) : post_likes(N)
- posts(1) : post_comments(N)

## 2. 기능적 역할
- 작성자 기준 조회가 빈번해 member_id 기반 인덱스를 둔다.
- like/comment 집계는 동시성 고려해 별도 컬럼으로 관리한다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 게시글 ID
2 | member_id | BIGINT | nn | 작성자 ID
3 | content | VARCHAR(200) | nn | 본문
4 | like_count | BIGINT | nn, default 0 | 좋아요 수
5 | comment_count | BIGINT | nn, default 0 | 댓글 수
6 | created_at | DATETIME | nn | 작성일
7 | updated_at | DATETIME | nn | 수정일

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `idx_posts_member_created (member_id, created_at)` | 작성자별 게시글 목록 최신순 조회와 사용자 프로필/홈 피드 조회를 최적화하기 위한 인덱스 |
| `idx_posts_created (created_at)` | 전체 게시글 최신순 목록, 홈 피드, 검색 후 정렬에서 최신 게시글을 빠르게 찾기 위한 인덱스 |

## 5. 운영 정책
- 게시글 삭제 시 댓글/좋아요/태그는 별도 정리 로직으로 삭제한다.
- 좋아요/댓글 수는 동시성 대응을 위해 DB 업데이트 쿼리로 증감한다.
