# chat_members

## 테이블 목적
- 채팅방 참여자 목록을 관리한다.

## 컬럼 정의
| 컬럼명 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AUTO_INCREMENT | 참여 식별자 |
| room_id | BIGINT | NOT NULL | 채팅방 ID |
| member_id | BIGINT | NOT NULL | 참여 회원 ID |
| joined_at | DATETIME | NOT NULL | 참여 시각 |

## 인덱스/제약
- UNIQUE: `(room_id, member_id)` 중복 참여 방지
- INDEX: `member_id` 기준 조회

## 관계
- `room_id` → `chat_rooms.id`
- `member_id` → `members.id`

## 운영 정책
- 참여/퇴장 이력 저장 여부는 추후 결정한다.
- 강제 퇴장/차단 정책은 서비스 정책에 따라 확정한다.
