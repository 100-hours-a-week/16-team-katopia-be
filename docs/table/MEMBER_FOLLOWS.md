# Member Follows

## 1. 테이블 설명
사용자 간 팔로우 관계를 저장하는 테이블.  
members와 자기참조 N:M 관계를 표현하며, 중복 팔로우를 방지한다.

### 연관관계
- members(1) : member_follows(N) (follower 기준)
- members(1) : member_follows(N) (followed 기준)

## 2. 기능적 역할
- 관계 자체가 엔티티이므로 대리키(id) 사용 + (follower_id, followed_id) 유니크 제약 적용.
- 셀프 팔로우는 서비스 정책으로 차단.
- 목록 조회는 시간순(최신순) 정렬을 사용하므로 created_at 인덱스를 포함한다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 식별자
2 | follower_id | BIGINT | fk, nn | 팔로우 행위자
3 | followed_id | BIGINT | fk, nn | 팔로우 대상자
4 | created_at | DATETIME | nn | 팔로우 시각

## 4. 인덱스/제약
- `uidx_member_follows_follower_followed (follower_id, followed_id)`  
  - 중복 팔로우 방지(정합성) + 팔로잉 여부 확인(검색 결과에서 isFollowing 계산) 최적화.
- `idx_member_follows_follower_created (follower_id, created_at)`  
  - 팔로잉 목록 최신순 조회 및 커서 페이징 최적화.
- `idx_member_follows_followed_created (followed_id, created_at)`  
  - 팔로워 목록 최신순 조회 및 커서 페이징 최적화.

## 5. 운영 정책
- 팔로우/언팔로우는 분리 API로 제공
- 팔로우 시 알림 저장은 추후 알림 테이블 도입 후 추가 예정
- 탈퇴 시 팔로우 관계는 일괄 삭제
