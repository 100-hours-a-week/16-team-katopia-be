# Vote Participations

## 1. 테이블 설명
사용자가 특정 투표에 참여했는지 여부를 저장한다.
동일 사용자/투표 중복 참여는 유니크 제약으로 차단한다.

### 연관 관계
- votes(1) : vote_participations(N)

## 2. 기능적 역할
- 중복 참여 차단
- 결과 조회 권한 판단

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 참여 기록 ID
2 | vote_id | BIGINT | nn | 투표 ID
3 | member_id | BIGINT | nn | 참여자 ID
4 | completed_at | DATETIME | nn | 참여 완료 시각

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `uidx_vote_participations_vote_member (vote_id, member_id)` | 동일 사용자의 동일 투표 중복 참여를 막고, 결과 조회 권한 확인을 빠르게 처리하기 위한 유니크 제약 |

## 5. 운영 정책
- 항목별 선택 로그가 필요하면 별도 로그 테이블을 추가한다.
- 다중 회차 투표가 필요하면 회차 컬럼을 추가한다.
