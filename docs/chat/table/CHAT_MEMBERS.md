# Chat Members

## 1. 테이블 설명
MongoDB `chat_members` 컬렉션으로, 채팅방 참여자 정보를 저장한다.

### 연관 관계
- chat_members(N) : chat_rooms(1)
- chat_members(N) : members(1)

## 2. 기능적 역할
- 채팅방 참여/퇴장 상태를 기록하고 입장 권한 판단에 사용한다.
- 마지막 읽음 메시지 기준(last_read_message_id)으로 안읽음 수를 계산한다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | ObjectId(String) | pk, nn | Mongo 문서 식별자
2 | room_id | ObjectId(String) | nn | 채팅방 ID
3 | member_id | BIGINT | nn | 참여 회원 ID
4 | joined_at | DATETIME(Instant) | nn | 참여 시각
5 | realtime_notification_enabled | BOOLEAN | nn, default true | 채팅방 실시간 알림 수신 여부
6 | last_read_message_id | BIGINT | null 가능 | 마지막으로 읽은 메시지 ID

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `uidx_chat_members_room_member (room_id, member_id)` | 같은 사용자의 동일 방 중복 참여를 막고, 참여 여부 확인을 단건 조회로 처리하기 위한 유니크 제약 |
| `idx_chat_members_member (member_id)` | "내가 참여한 방 목록" 조회에서 member 기준 필터가 반복되므로 기본 조회 비용을 줄이기 위한 인덱스 |
| `idx_chat_members_room_last_read (room_id, last_read_message_id)` | 방 단위 읽음 상태 집계와 lastReadMessageId 비교 기반 안읽음 계산을 보조하기 위한 인덱스 |

## 5. 운영 정책
- 퇴장 시 참여 문서를 삭제하고, 재참여 시 새로 생성한다.
- 채팅방 참여 전에는 입장을 허용하지 않는다.
- 실시간 알림 OFF는 SSE 채팅 알림만 차단하며, 채팅 수신/접속은 유지한다.
- last_read_message_id는 안읽음 수 계산의 기준 값으로 사용한다.
- 새 참여 문서는 `realtime_notification_enabled=true`, `last_read_message_id=null`로 생성한다.
