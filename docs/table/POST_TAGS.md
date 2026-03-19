# Post Tags

## 1. 테이블 설명
게시글-태그 연결 테이블.

### 연관 관계
- posts(1) : post_tags(N)
- tags(1) : post_tags(N)

## 2. 기능적 역할
- 동일 게시글에 동일 태그 중복을 유니크 제약으로 방지한다.
- 태그 기준 검색 최적화를 위해 tag_id 기반 인덱스를 둔다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 식별자
2 | post_id | BIGINT | nn | 게시글 ID
3 | tag_id | BIGINT | nn | 태그 ID

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `uidx_post_tags_post_tag (post_id, tag_id)` | 동일 게시글에 같은 태그가 중복 연결되는 것을 막기 위한 유니크 제약 |
| `idx_post_tags_tag_post (tag_id, post_id)` | 태그 기준 게시글 검색과 태그별 역조회 성능을 확보하기 위한 인덱스 |

## 5. 운영 정책
- 게시글 태그 변경 시 diff 기반으로 삭제/추가한다.
- 태그명은 저장 전 `#` 제거로 정규화한다.
