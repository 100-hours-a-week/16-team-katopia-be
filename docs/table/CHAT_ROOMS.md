# chat_rooms

## 테이블 목적
- 그룹 채팅 방의 기본 정보를 저장한다.

## 컬럼 정의
| 컬럼명 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AUTO_INCREMENT | 채팅방 식별자 |
| title | VARCHAR(100) | NULL | 채팅방 제목(옵션) |
| created_at | DATETIME | NOT NULL | 생성 시각 |

## 인덱스/제약
- PK: `id`

## 관계
- `chat_members.room_id` → `chat_rooms.id`
- `chat_messages.room_id` → `chat_rooms.id`

## 운영 정책
- 방 삭제 정책은 논의 후 확정한다(soft delete 여부 포함).
- 방 제목은 필수값이 아니며, 클라이언트 정책에 따라 표시한다.
