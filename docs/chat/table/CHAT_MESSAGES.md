# Chat Messages

## 1. 테이블 설명
MongoDB `chat_messages` 컬렉션으로, 채팅 메시지 본문과 메타 정보를 저장한다.

### 연관 관계
- chat_messages(N) : chat_rooms(1)
- chat_messages(N) : members(1)

## 2. 기능적 역할
- 메시지 본문/이미지 정보를 저장하고 방 단위 최신 조회를 지원한다.
- 발신자 닉네임/프로필 이미지 스냅샷을 저장해 조회 성능을 확보한다.
- 이미지 메시지는 message="사진" + image_object_key 조합으로 구분한다.
- `message_id`를 읽음 처리 및 메시지 정렬 기준으로 사용한다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | ObjectId(String) | pk, nn | Mongo 문서 식별자
2 | room_id | ObjectId(String) | nn | 채팅방 ID
3 | sender_id | BIGINT | nn | 발신자 ID
4 | message_id | BIGINT | unique, nn | 읽음 처리와 정렬 기준이 되는 전역 메시지 ID
5 | sender_nickname_snapshot | VARCHAR | nn | 발신자 닉네임 스냅샷
6 | sender_profile_image_object_key_snapshot | VARCHAR(1024) | null 가능 | 발신자 프로필 이미지 스냅샷
7 | message | VARCHAR | nn | 텍스트 본문 또는 이미지 메시지일 때 `"사진"`
8 | image_object_key | VARCHAR(1024) | null 가능 | 이미지 메시지일 때 S3 오브젝트 키
9 | message_type | VARCHAR(20) | nn | TEXT / IMAGE
10 | created_at | DATETIME(Instant) | nn | 생성 시각

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `uidx_chat_messages_message_id (message_id)` | 읽음 처리와 메시지 정렬 기준으로 쓰는 전역 messageId의 중복 생성을 막고, ACK 검증을 단건 조회로 처리하기 위한 유니크 제약 |
| `idx_chat_messages_room_message (room_id, message_id desc)` | 방 단위 최신 메시지 조회와 `message_id` 기반 커서 페이징을 동시에 지원하기 위한 복합 인덱스 |

## 5. 운영 정책
- 메시지 저장은 현재 동기 저장(write-through) 후 전송 구조를 사용한다.
- 순서 보장은 `room_id + message_id` 기준으로 유지한다.
- 이미지 전송은 message='사진', image_object_key 채움으로 구분한다.
- 문서 `_id`는 Mongo 기본 ObjectId 자동 생성 방식을 사용하고, 읽음/정렬 기준은 `_id`가 아니라 `message_id`를 사용한다.
