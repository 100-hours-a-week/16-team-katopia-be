# 투표 도메인 테크 스펙 (Vote)

## 배경 (Background)

- 이미지 기반 투표를 제공한다.
- 참여자는 항목별로 "어울려요"를 선택하며, 결과는 작성자/참여자에게만 공개한다.

## 목표가 아닌 것 (Non-goals)

- 실시간 투표 스트리밍
- 다중 선택 외의 가중치/순위 투표
- n초 단위 결과 푸시

## 핵심 정책

- 투표 생성 시 제목 + 이미지 오브젝트 키 목록(2~5)을 입력한다.
- 투표는 생성 시점으로부터 24시간 후 자동 종료된다.
- 참여자는 1회만 참여 가능하며, 참여 완료 후 결과를 응답으로 받는다.
- 결과는 작성자 또는 참여자에게만 공개한다(비참여자는 403).
- 득표율은 전체 득표수 대비 비율(%)을 소수점 둘째 자리 반올림으로 제공한다.
- 투표 삭제는 하드 삭제이며, 종료 후에도 삭제 가능하다.

## 데이터 스키마 (ERD/DDL)

```sql
CREATE TABLE votes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  title VARCHAR(20) NOT NULL,
  created_at DATETIME NOT NULL,
  expires_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_votes_author_created (member_id, created_at)
);

CREATE TABLE vote_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  vote_id BIGINT NOT NULL,
  image_object_key VARCHAR(1024) NOT NULL,
  sort_order TINYINT NOT NULL,
  fit_count BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_vote_items_vote_order (vote_id, sort_order)
);

CREATE TABLE vote_participations (
  id BIGINT NOT NULL AUTO_INCREMENT,
  vote_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  completed_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uidx_vote_participations_vote_member (vote_id, member_id)
);
```

## 집계 정책

- 투표 참여 시 선택한 항목의 `fit_count`를 일괄 증가시킨다.
- 득표율 계산: `(item.fitCount / totalCount) * 100` → 소수점 둘째 자리 반올림.
- totalCount가 0이면 모든 득표율은 0.00으로 반환한다.

## API 명세

- 투표 생성: `POST /api/votes`
- 내 투표 목록(커서): `GET /api/votes?size=&after=`
- 참여 가능한 최신 투표: `GET /api/votes/candidates`
- 투표 결과 조회(작성자/참여자): `GET /api/votes/{id}`
- 투표 참여: `POST /api/votes/{id}/participations`
- 투표 삭제(작성자): `DELETE /api/votes/{id}`

## 종료 처리

- `expires_at` 기준으로 종료 여부를 계산한다.

## 예외 처리

- 제목 검증 실패: 400
- 이미지 개수 검증 실패: 400
- 중복 참여: 409
- 종료된 투표 참여: 409
- 작성자/참여자 외 결과 조회: 403
