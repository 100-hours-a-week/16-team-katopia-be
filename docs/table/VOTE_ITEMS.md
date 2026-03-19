# Vote Items

## 1. 테이블 설명
투표 항목(이미지)과 득표수를 저장한다.
투표 1건에 2~5개의 항목이 존재하며, 항목 순서는 sort_order로 관리한다.

### 연관 관계
- votes(1) : vote_items(N)

## 2. 기능적 역할
- 투표 항목 이미지/순서를 제공한다.
- 항목별 득표수를 집계한다.
- 동일 투표 내 sort_order 중복을 유니크 제약으로 방지한다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 항목 식별자
2 | vote_id | BIGINT | nn | 투표 ID
3 | image_object_key | VARCHAR(1024) | nn | 이미지 키
4 | sort_order | TINYINT | nn | 항목 순서
5 | fit_count | BIGINT | nn, default 0 | 득표수

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `uk_vote_items_vote_order (vote_id, sort_order)` | 동일 투표 안에서 항목 순서가 중복되지 않도록 보장하기 위한 유니크 제약 |

## 5. 운영 정책
- 항목 메타데이터 확장이 필요하면 텍스트/카테고리 컬럼을 추가한다.
- 득표 유형 확장이 필요하면 집계 컬럼을 추가한다.
