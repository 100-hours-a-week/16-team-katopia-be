# 알림(Notification)

## 알림 서비스(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-NOTIFICATION-S-01 | 알림 읽음 처리 성공 | 알림 존재 | markRead 호출 | readAt 갱신 |
| TC-NOTIFICATION-S-02 | 미읽음 알림 목록 조회 성공 | 미읽음 존재 | getLatestUnread 호출 | 목록 반환 |
| TC-NOTIFICATION-F-01 | 알림 읽음 처리 실패(알림 없음) | 알림 미존재 | markRead 호출 | NOTIFICATION-E-001 반환 |
| TC-NOTIFICATION-F-02 | 알림 읽음 처리 실패(이미 읽음) | readAt 존재 | markRead 호출 | NOTIFICATION-E-002 반환 |

## 알림 SSE(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-NOTIFICATION-SSE-S-01 | SSE 연결 시 emitter 저장 | 신규 연결 | connect 호출 | emitter 저장 |
| TC-NOTIFICATION-SSE-S-02 | 동일 사용자 SSE 연결 교체 | 기존 연결 존재 | connect 호출 | emitter 교체 |
| TC-NOTIFICATION-SSE-S-03 | 미연결 대상 전송 무시 | emitter 없음 | send 호출 | 예외 없음 |
| TC-NOTIFICATION-SSE-S-04 | SSE 발행은 수신자 기준 | 알림 존재 | publish 호출 | recipientId로 send |
| TC-NOTIFICATION-SSE-S-05 | SSE 연결 시 미읽음 전송 시도 | 미읽음 2건 | connect 호출 | send 2회 호출 |

## 알림 트리거(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-TRIGGER-S-01 | 팔로우 생성 시 알림 트리거 | actor/recipient 존재 | createFollow 호출 | 알림 저장 |
| TC-TRIGGER-S-02 | 투표 종료 시 알림 트리거 | recipient 존재 | createVoteClosed 호출 | 알림 저장 |
| TC-TRIGGER-S-03 | 게시글 좋아요 시 알림 트리거 | 타인 게시글 | like 호출 | 알림 생성 요청 |
| TC-TRIGGER-S-04 | 댓글 생성 시 알림 트리거 | 타인 게시글 | create 호출 | 알림 생성 요청 |
| TC-TRIGGER-S-05 | 본인 행위는 알림 미생성 | actor=recipient | createFollow 호출 | 저장 없음 |
| TC-TRIGGER-S-06 | 게시글 좋아요 첫 이미지 스냅샷 저장 | 게시글 이미지 존재 | createPostLike 호출 | imageObjectKeySnapshot 저장 |
| TC-TRIGGER-S-07 | 댓글 생성 첫 이미지 스냅샷 저장 | 게시글 이미지 존재 | createPostComment 호출 | imageObjectKeySnapshot 저장 |
