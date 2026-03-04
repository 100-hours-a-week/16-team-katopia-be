# chat_messages

## 테이블 목적
- 채팅 메시지 본문과 메타 정보를 저장한다.

## 컬럼 정의
| 컬럼명 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AUTO_INCREMENT | 메시지 ID |
| room_id | BIGINT | NOT NULL | 채팅방 ID |
| sender_id | BIGINT | NOT NULL | 발신자 ID |
| message_idempotency_key | VARCHAR(64) | NULL | 중복 전송 방지 키 |
| content | VARCHAR(1000) | NOT NULL | 메시지 본문 |
| created_at | DATETIME | NOT NULL | 생성 시각 |

## 인덱스/제약
- INDEX: `(room_id, created_at)` 방 단위 최신 조회
- INDEX: `sender_id`

## 관계
- `room_id` → `chat_rooms.id`
- `sender_id` → `members.id`

## 운영 정책
- 메시지 저장은 비동기 처리한다.
- 순서 보장은 `room_id + created_at` 기준으로 유지한다.
- message_idempotency_key는 클라이언트 재전송 방지에 사용한다.
