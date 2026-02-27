# count_batches

## 1) 테이블 설명
댓글 집계 배치 이력(Delta 적용 결과)을 기록하는 테이블이다.  
향후 좋아요/게시글/팔로우 등 다른 집계 배치 이력까지 확장 가능한 범용 네이밍을 사용한다.

### 연관 관계
없음(집계 배치 이력 전용)

## 2) 기능적 역할
- 댓글 집계 배치 실행 이력 저장
- 마지막 적용 시각(커서) 기준으로 다음 배치 범위 결정
- Redis 장애/복구 시 재계산 기준점 제공

## 3) 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명 | 설계 근거
---|---|---|---|---|---
1 | id | BIGINT | pk, auto_increment | 배치 이력 ID | 운영/추적용 식별자
2 | processed_from | DATETIME | nn | 배치 시작 기준 시각 | 재계산 범위 시작점
3 | processed_to | DATETIME | nn | 배치 종료 기준 시각 | 다음 배치 커서/복구 기준
4 | applied_post_count | INT | nn | 반영된 게시글 수 | 처리 규모/모니터링 지표
5 | applied_delta | INT | nn | 반영된 댓글 delta 합 | 집계 적용량 확인
6 | created_at | DATETIME | nn | 배치 완료 시각 | 운영 기록/모니터링

## 4) 인덱스 정의 및 근거
인덱스 정의 | 사용 근거
---|---
pk_count_batches_id (id) | 기본 PK
idx_count_batches_processed_to (processed_to) | 최신 배치 조회 및 커서 기준 조회 최적화

## 5) 운영 정책
- 배치는 `processed_to`를 마지막 기준 시각으로 저장한다.
- 다음 배치는 마지막 `processed_to` 이후 데이터를 기준으로 범위를 계산한다.
- Redis 장애 시 마지막 `processed_to` 이후 범위로 재계산한다.
