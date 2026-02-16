# Notifications

## 1) 테이블 설명
사용자에게 전달되는 알림 내역을 저장하는 테이블.  
알림은 이벤트 스냅샷(행위자 닉네임/이미지, 메시지)을 보관하며, 조회는 수신자 기준 최신순으로 제공한다.

## 2) 관계
- members(1) : notifications(N) (recipient 기준)
- members(1) : notifications(N) (actor 기준, nullable)

## 3) 설계 근거
- 알림은 이벤트성 로그이므로 대리키(id) 사용.
- actor 정보는 스냅샷으로 저장해 닉네임/이미지 변경 영향 최소화.
- ref_id로 대상 리소스(게시글/투표 등) 연결.
- 알림 센터 목록 조회를 위해 수신자 기준 최신순 정렬/페이징을 고려.
- ref_id는 다수 테이블의 FK를 담지만, 타입은 애플리케이션 계층에서 분기해 해석하므로 정합성 이슈가 없다.

## 4) 컬럼 정의
| No | 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- | --- |
| 1 | id | BIGINT | pk, nn | 알림 식별자 |
| 2 | recipient_id | BIGINT | fk, nn | 수신자 |
| 3 | actor_id | BIGINT | fk | 행위자(시스템 알림은 null) |
| 4 | notification_type | ENUM(NotificationType) | nn | 알림 유형 |
| 5 | actor_nickname_snapshot | VARCHAR(20) | nn | 행위자 닉네임 스냅샷 |
| 6 | image_object_key_snapshot | VARCHAR(1024) |  | 알림 이미지 스냅샷(오브젝트 키) |
| 7 | message | VARCHAR(255) | nn | 알림 메시지 |
| 8 | ref_id | BIGINT | nn | 대상 리소스 ID |
| 9 | created_at | DATETIME | nn | 생성 시각 |
| 10 | read_at | DATETIME |  | 읽음 시각 |

### 알림 유형(NotificationType)
| 유형 | 의미 |
| --- | --- |
| FOLLOW | 다른 사용자가 나를 팔로우 |
| POST_LIKE | 다른 사용자가 내 게시글을 좋아요 |
| POST_COMMENT | 다른 사용자가 내 게시글에 댓글 작성 |
| VOTE_CLOSED | 내가 만든 투표 또는 참여했던 투표가 종료 |

## 5) 인덱스/제약
- `idx_notifications_recipient_created (recipient_id, created_at)`: 수신자별 최신순 목록 조회 최적화.

## 6) 운영 정책
- 읽음(read_at) 기록 후 7일 경과 시 삭제.
- 미읽음은 삭제하지 않고 유지.
- 알림 내용 보존을 위해 스냅샷(actor 닉네임/이미지) 저장
- 알림 이미지 스냅샷은 유형별로 다르게 채운다(FOLLOW: actor 프로필, POST_LIKE/POST_COMMENT: 게시글 첫 이미지, VOTE_CLOSED: 투표 첫 이미지).
