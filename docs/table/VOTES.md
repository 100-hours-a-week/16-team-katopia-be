# votes

## 1. 테이블 설명

사용자(member)가 생성한 투표(vote) 정보를 저장하는 테이블입니다.  
투표에 사용되는 이미지와 “어울려요” 집계는 `vote_items`([VOTE_ITEMS.md](/VOTE_ITEMS.md))에서 정의합니다.  
투표 참여 정보는 `vote_participations`([VOTE_PARTICIPATIONS.md](/VOTE_PARTICIPATIONS.md))에서 정의합니다.  

### 연관관계
- vote(N):member(1)
- vote(1):vote_participations(N)
- vote(1):vote_items(N) {N: 2 ~ 5} 


## 2. 기능적 역할

- 투표 생성/삭제/종료
    - 투표 제목과 이미지(`2 ~ 5개`)를 업로드할 수 있습니다.
    - 이미지 업로드 개수 제한은 `애플리케이션 계층 검증으로 보장`합니다.
    - 한 번 생성된 투표는 `수정이 불가능`합니다.
    - 투표는 `생성 시각 기준 24시간 후 자동 종료`되며, `expires_at` 기준으로 종료 여부를 판단합니다.
- 내 투표 목록 조회(시간순)
- 자동 종료 대상 식별(스케줄러)

## 3. 컬럼 정의

| No | 컬럼 | 타입 | 제약 | 설명 | 설계 근거 |
| --- | --- | --- | --- | --- | --- |
| 1 | id | BIGINT | pk, nn | 투표 식별자 | 대리키 |
| 2 | member_id | BIGINT | fk, nn | 작성자 ID | 작성자 기준 조회 |
| 3 | title | VARCHAR(20) | nn | 투표 제목 | 요구사항 최대 20자 |
| 4 | created_at | DATETIME | nn | 생성 시각 | 목록/정렬 기준 |
| 5 | expires_at | DATETIME | nn | 만료 시각 | 24시간 종료 정책 |

## 4. 인덱스 정의 및 근거

| 인덱스 정의 | 사용 근거 |
| --- | --- |
| `idx_votes_author_created (member_id, created_at)` | 내 투표 목록 조회 시 `WHERE member_id=? ORDER BY created_at DESC` 최적화 |

## 5. 향후 확장성 고려

| 확장 | 방안 |
| --- | --- |
| 종료 정책 다양화 | `expires_at` 계산 방식 확장 |
| 공개 범위/상태 | 상태 필드 추가 |
