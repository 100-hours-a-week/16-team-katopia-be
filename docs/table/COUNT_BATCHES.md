# Count Batches

## 1. 테이블 설명
댓글 집계 배치 이력(Delta 적용 결과)을 기록하는 테이블이다.
향후 좋아요/게시글/팔로우 등 다른 집계 배치 이력까지 확장 가능한 범용 네이밍을 사용한다.

### 연관 관계
- 없음(집계 배치 이력 전용)

## 2. 기능적 역할
- 댓글 집계 배치 실행 이력을 저장한다.
- 마지막 적용 시각(커서) 기준으로 다음 배치 범위를 결정한다.
- Redis 장애/복구 시 재계산 기준점을 제공한다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 배치 이력 ID
2 | processed_from | DATETIME | nn | 배치 시작 기준 시각
3 | processed_to | DATETIME | nn | 배치 종료 기준 시각
4 | applied_post_count | INT | nn | 반영된 게시글 수
5 | applied_delta | INT | nn | 반영된 댓글 delta 합
6 | created_at | DATETIME | nn | 배치 완료 시각

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `pk_count_batches_id (id)` | 배치 이력 단건 식별과 정렬의 기본 전제가 되는 기본키 |
| `processed_to` 보조 인덱스 미적용 | 현재 코드는 `findTopByOrderByIdDesc()`로 마지막 배치를 조회하므로, 엔티티/JPA 기준으로 `processed_to` 보조 인덱스는 아직 정의하지 않았다. 규모 확대 시 재검토 대상이다. |

## 5. 운영 정책
- 배치는 processed_to를 마지막 기준 시각으로 저장한다.
- 다음 배치는 마지막 processed_to 이후 데이터를 기준으로 범위를 계산한다.
- Redis 장애 시 마지막 processed_to 이후 범위로 재계산한다.
