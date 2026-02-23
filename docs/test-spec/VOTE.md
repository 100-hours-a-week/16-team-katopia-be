# 투표(Vote)

## 투표 서비스(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-VOTE-S-01 | 투표 생성: 이미지 키 반환 | 유효 요청 | create 호출 | 이미지 키 목록 반환 |
| TC-VOTE-S-02 | 내 투표 목록: 커서 생성 | 투표 1개 | listMine 호출 | nextCursor 생성 |
| TC-VOTE-S-03 | 참여 가능한 최신 투표 조회 | 최신 투표 존재 | findLatestCandidate 호출 | 후보 + 아이템 반환 |
| TC-VOTE-S-04 | 투표 참여: 결과 반환 | 투표/아이템 유효 | participate 호출 | 결과 + 참여 저장 |
| TC-VOTE-F-01 | 투표 참여 실패: 중복 투표 항목 | 동일 itemId 중복 | participate 호출 | VOTE_ITEM_DUPLICATED |
| TC-VOTE-F-02 | 투표 결과 조회 실패: 참여/작성자 아님 | 참여 이력 없음 | getResult 호출 | ACCESS_DENIED |
| TC-VOTE-F-03 | 투표 삭제 실패: 작성자 아님 | 타인 투표 | delete 호출 | ACCESS_DENIED |
