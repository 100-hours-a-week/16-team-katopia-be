# 팔로우 도메인 테크 스펙 (Follow)

## 배경 (Background)

- 프로젝트 목표: 팔로우/언팔로우와 목록/집계를 제공한다.
- 문제 정의:
  - 중복 팔로우와 자기 팔로우를 차단해야 한다.
  - 프로필/검색 응답에 팔로우 여부 표시가 필요하다.

## 목표가 아닌 것 (Non-goals)

- 차단/뮤트 기능
- 추천 알고리즘과의 결합

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 방향성 관계(단방향)
- 중복 팔로우 금지
- 자기 자신 팔로우 금지
- 팔로우/팔로잉 수 집계 컬럼 유지

### 데이터 스키마 (ERD/DDL)

```sql
CREATE TABLE member_follows (
  id BIGINT NOT NULL AUTO_INCREMENT,
  follower_id BIGINT NOT NULL,
  followed_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uidx_member_follows_follower_followed (follower_id, followed_id),
  KEY idx_member_follows_follower_created (follower_id, created_at),
  KEY idx_member_follows_followed_created (followed_id, created_at)
);

ALTER TABLE members
  ADD COLUMN follower_count BIGINT NOT NULL DEFAULT 0,
  ADD COLUMN following_count BIGINT NOT NULL DEFAULT 0;
```

### 집계 정책

- 팔로우 생성 시 양쪽 집계 증감
- 언팔로우 시 양쪽 집계 감소

## API 명세 (API Specifications)

- 팔로우: `POST /api/members/{memberId}/follow`
- 언팔로우: `DELETE /api/members/{memberId}/follow`
- 팔로잉 목록: `GET /api/members/{id}/followings`
- 팔로워 목록: `GET /api/members/{id}/followers`

## 트랜잭션/정합성 정책

- 팔로우 생성/해제는 원자적 업데이트 필요
- 집계 업데이트는 DB update 쿼리 사용

## 동시성/데드락 대응 (예정)

- 문제
  - 팔로우/언팔로우 시 멤버 집계 필드 업데이트가 동시에 발생하면 데드락 가능성이 있다.
- 대응 전략(계획)
  - 집계 업데이트는 항상 `min(id) -> max(id)` 순서로 락을 획득하도록 정렬한다.
  - 데드락 발생 시 1~2회 재시도(backoff 포함) 후 실패로 처리한다.

## 장애/예외 처리

- 중복 팔로우는 409
- 자기 팔로우는 400
- 대상 사용자 없음은 404

## 변경 사항

- 프로필/검색 응답에 `isFollowing` 추가 예정

## 용어 정의 (Glossary)

- follower: 나를 팔로우하는 사용자
- following: 내가 팔로우하는 사용자
