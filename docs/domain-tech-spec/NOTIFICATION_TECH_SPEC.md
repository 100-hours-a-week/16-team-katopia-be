# 알림 도메인 테크 스펙 (Notification)

## 배경 (Background)

- 사용자 행동에 따른 알림을 저장하고 목록을 제공한다.
- 실시간 전달/재시도 정책은 비동기 테크 스펙에서 다룬다.

## 목표가 아닌 것 (Non-goals)

- 모바일 푸시(Firebase)
- 개인화 추천 알림

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 저장형 알림(DB 저장 우선)
- 알림 목록 조회는 커서 기반 인피니티 스크롤
- 읽음 처리/보관 정책은 테이블 정의서 기준

### 데이터 스키마

- 테이블 정의서: `docs/table/NOTIFICATIONS.md`
- ref_id는 notification_type으로 해석해 대상 리소스를 식별한다.
- actor 정보는 스냅샷으로 저장한다.
- notification_type은 ENUM으로 관리한다.
- 투표 종료 알림은 투표 생성자와 참여자에게 발송한다.

## API 명세 (API Specifications)

- 알림 목록: `GET /api/notifications`
- 읽음 처리: `PATCH /api/notifications/{id}`

## 보안 고려사항

- 알림 조회는 인증 사용자만 허용

## 비동기 연동

- SSE/브로커/재전송 정책은 `docs/domain-tech-spec/ASYNC_TECH_SPEC.md` 참고
