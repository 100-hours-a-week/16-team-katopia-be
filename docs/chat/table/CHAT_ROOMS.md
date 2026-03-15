# Chat Rooms

## 1. 테이블 설명
MongoDB `chat_rooms` 컬렉션으로, 그룹 채팅 방의 기본 정보를 저장한다.

### 연관 관계
- chat_rooms(1) : chat_members(N)
- chat_rooms(1) : chat_messages(N)

## 2. 기능적 역할
- 채팅방의 기본 속성(제목/썸네일/생성자/참여 인원 수)을 관리한다.
- 생성자(owner_id)를 기준으로 수정/삭제 권한을 판단한다.
- 참여 인원 수는 목록/상세 조회에서 즉시 제공하기 위한 집계 값으로 사용한다.

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | ObjectId(String) | pk, nn | Mongo 문서 식별자, 채팅방 ID
2 | owner_id | BIGINT | nn | 채팅방 생성자 ID
3 | title | VARCHAR | nn | 채팅방 제목(필수)
4 | participant_count | INT | nn | 채팅방 참여 인원 수
5 | thumbnail_image_object_key | VARCHAR(1024) | - | 채팅방 썸네일 이미지 오브젝트 키
6 | created_at | DATETIME(Instant) | nn | 생성 시각
7 | updated_at | DATETIME(Instant) | nn | 수정 시각

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `idx_chat_rooms_owner (owner_id)` | 생성자 기준 수정/삭제 권한 확인과 "내가 만든 방" 조건 조회를 빠르게 처리하기 위한 최소 인덱스 |
| `participant_count` 인덱스 미도입 | 현재 코드는 참여 인원 수 범위 검색/정렬보다 방 단건 조회와 생성자 권한 확인이 우선이므로 유지 비용 대비 효익이 낮다 |
| `updated_at` 인덱스 미도입 | `updated_at` 기반 전체 방 커서 조회 요구가 문서/코드에 완전히 고정되지 않아, 실제 목록 조회 정책 확정 후 재검토하는 편이 낫다 |

## 5. 운영 정책
- 방 삭제는 하드 삭제를 기본으로 한다(삭제 시 메시지/참여 정보 함께 삭제).
- 방 제목은 필수값으로 강제한다.
- 참여 인원 수는 방 생성 시 `1`로 시작하고, 참여/퇴장 시 실시간 갱신한다.
- 문서 `_id`는 Mongo 기본 ObjectId 자동 생성 방식을 사용한다.
