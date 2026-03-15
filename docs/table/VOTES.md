# Votes

## 1. 테이블 설명
사용자가 생성한 투표 정보를 저장한다.
투표 항목/이미지는 vote_items, 참여 정보는 vote_participations에서 관리한다.

### 연관 관계
- members(1) : votes(N)
- votes(1) : vote_participations(N)
- votes(1) : vote_items(N)

## 2. 기능적 역할
- 투표 생성/삭제/종료 관리
- 제목과 이미지(2~5개) 업로드, 개수 제한은 애플리케이션 계층 검증으로 보장
- 한 번 생성된 투표는 수정 불가
- expires_at 기준으로 종료 여부를 판단
- 내 투표 목록 최신순 조회
- 자동 종료 대상 식별(스케줄러)

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 투표 식별자
2 | member_id | BIGINT | nn | 작성자 ID
3 | title | VARCHAR(20) | nn | 투표 제목
4 | created_at | DATETIME | nn | 생성 시각
5 | expires_at | DATETIME | nn | 만료 시각

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `idx_votes_author_created (member_id, created_at)` | 내 투표 목록 최신순 조회와 커서/페이지네이션 정렬을 효율적으로 처리하기 위한 인덱스 |

## 5. 운영 정책
- 종료 정책 다양화가 필요하면 expires_at 계산 방식을 확장한다.
- 공개 범위/상태 확장이 필요하면 상태 필드를 추가한다.
