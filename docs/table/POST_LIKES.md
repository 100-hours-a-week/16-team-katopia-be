# Post Likes

## 1. 테이블 설명
게시글 좋아요 관계 테이블.

### 연관 관계
- members(1) : post_likes(N)
- posts(1) : post_likes(N)

## 2. 기능적 역할
- 중복 좋아요를 유니크 제약으로 방지한다.
- 좋아요 여부 조회를 위해 member_id, post_id 복합 인덱스를 둔다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 식별자
2 | member_id | BIGINT | nn | 회원 ID
3 | post_id | BIGINT | nn | 게시글 ID
4 | created_at | DATETIME | nn | 좋아요 시각

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `uidx_post_likes_member_post (member_id, post_id)` | 같은 사용자의 동일 게시글 중복 좋아요를 막고, 좋아요 여부 확인을 빠르게 처리하기 위한 유니크 제약 |
| `idx_post_likes (member_id, post_id)` | 애플리케이션 코드와 쿼리 계획에서 member/post 조합 조회를 안정적으로 처리하기 위한 보조 인덱스 |

## 5. 운영 정책
- 좋아요 중복 방지, 좋아요 해제 시 즉시 삭제한다.
- 좋아요 수는 별도 집계 컬럼(posts.like_count)으로 관리한다.
