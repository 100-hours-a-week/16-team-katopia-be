# 접근 정책 가이드

## 1. 목적
- 로그인/비로그인 접근 가능 범위를 명확히 정의한다.
- 보안 설정(SecurityConfig)과 API 문서를 일치시킨다.
- 팀 내 공유 기준을 제공해 구현/리뷰/테스트 혼선을 줄인다.

## 2. 정책 정의
| 정책                 | 설명                    | 표시 | 
|--------------------|-----------------------|----|
| 공개 | Access Token 없이 접근 가능 | 🟢 |
| 인증 | 로그인 필요                | 🟡 | 
| 비공개 | -                     | 🔴 |

## 3. 접근 정책 표


| 경로 | 메서드  | 운영서버 | 개발서버 | 비고                     |
|---|------|------|------|------------------------|
| /api/actuator/health | GET  | 🟢   | 🟢|  공개                    | 헬스체크                   |
| /api/actuator/metrics | GET  | 🔴   | 🟢   | 메트릭(개발/로컬만)            |
| /api/actuator/prometheus | GET  | 🔴   | 🟢   | 프로메테우스(개발/로컬만)        |
| /api/swagger-ui/** | GET  | 🔴   | 🟢   | Swagger UI(개발/로컬만)   |
| /oauth2/** | ALL  | 🟢   | 🟢   | OAuth2 진입              |
| /login/** | ALL  | 🟢   | 🟢   | OAuth2 기본 경로           |
| /api/members/check | GET  | 🟢   | 🟢   | 닉네임 중복 체크              |
| /api/members/* | GET  | 🟢   | 🟢   | 공개 프로필 조회              |
| /api/members | POST | 🟢   | 🟢   | 회원가입 완료(등록 토큰 필요)      |
| /api/members/*/posts | GET  | 🟢 | 🟢   | 사용자 게시글 조회             |
| /api/posts | GET  | 🟡   | 🟡   | 게시글 목록 조회 |
| /api/posts/* | GET  | 🟢   | 🟢   | 게시글 세부 조회 |
| /api/posts/*/comments | GET  | 🟢   | 🟢   | 게시글 댓글 목록              |
| /api/search/** | GET  | 🟡   | 🟡   | 계정/게시글/태그 검색 |
| /api/members/me | GET  | 🟡   | 🟡   | 내 정보 조회                |
| /api/auth/tokens | POST | 🟢   | 🟢   | 토큰 재발급(RT 필요)          |
| /api/uploads/presign | POST | 🟡   | 🟡   | 이미지 업로드 Presign URL 발급 |
| /api/dev/** | ALL  | 🔴   | 🟢   | dev/local 프로필에서만 허용 |
| 그 외 모든 경로 | ALL  | 🟡   | 🟡   | 기본 정책                  |

## 4. 예외/특이 케이스
- 회원가입은 공개 경로지만 Registration Token이 필수임.
- 개발용 API는 개발/로컬 프로필에서만 로드됨.

## 5. 보안 설정 근거
- SecurityConfig의 requestMatchers 정책과 1:1로 매핑한다.
- 변경 시 본 문서를 먼저 갱신한다.

## 6. 변경 이력
- 2026-01-30: 초안 작성
