# 업로드/Presign 도메인 테크 스펙 (Upload/Presign)

## 배경 (Background)

- 프로젝트 목표: 클라이언트가 직접 S3로 업로드하도록 presigned URL을 발급한다.
- 문제 정의:
  - 서버 경유 업로드는 비용/지연이 크다.
  - 카테고리별 개수/확장자 제한이 필요하다.

## 목표가 아닌 것 (Non-goals)

- 서버 측 이미지 리사이징/검증
- 업로드 완료 콜백 처리

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 요청 바디: category + extensions[]
- 카테고리별 최대 개수
  - PROFILE: 1
  - POST: 3
  - VOTE: 5
- 허용 확장자: jpg, jpeg, png, heic, webp
- 만료 기본값: 10분
- 응답: uploadUrl + imageObjectKey

### objectKey 규칙

- 포맷: `{folder}/{memberId}/{epoch}-{uuid}.{ext}`

### 보안 고려사항

- presign은 인증 사용자만 허용
- bucket/만료 시간 설정은 서버 기준으로 강제

## API 명세 (API Specifications)

- presign 발급: `POST /api/uploads/presign`

## 장애/예외 처리

- category/extension/count 위반은 400
- 설정 누락은 400

## 용어 정의 (Glossary)

- objectKey: S3 객체 키
