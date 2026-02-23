# Refresh Tokens

## 1) 테이블 설명
JWT 기반 인증에서 Refresh Token Rotation(RTR) 적용과 RT의 유효/폐기 상태를 저장하는 테이블입니다.  
Access Token(AT)은 서버 저장 없이 서명/만료 검증으로 처리하고, RT는 장기 유효하므로, RTR에서는 `사용 시 교체(rotate) 및 이전 토큰 폐기`가 필요하므로
서버가 토큰의 유효성/폐기 여부를 상태로 추적해야 합니다.


### 연관 관계
members(1) : refresh_tokens(N)


## 2) 기능적 역할
- refresh token 유효성 판단(만료/폐기)
- RTR에서 사용된 토큰 폐기 + 폐기 토큰 재사용 차단
- 만료 토큰 정리(배치)
- 재사용 감지 시 사용자 전체 토큰 폐기(정책)

## 3) 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명 | 설계 근거
---|---|---|---|---|---
1 | id | BIGINT | pk, nn | 토큰 식별자 | 운영/감사/추적에 유리
2 | member_id | BIGINT | nn | 토큰 소유자 | 사용자 1명당 N개 세션 허용
3 | token_hash | VARCHAR(64) | unique, nn | refresh token 해시 | 원문 저장 금지, SHA-256 hex 64자리
4 | created_at | DATETIME | nn | 발급 시각 | 발급 기준 조회/관리
5 | expires_at | DATETIME | nn | 만료 시각 | 만료 검증/정리 기준
6 | revoked_at | DATETIME |  | 폐기 시각 | RTR rotate 시 이전 토큰 즉시 폐기

## 4) 인덱스 정의 및 근거
인덱스 정의 | 사용 근거
---|---
uk_refresh_tokens_token_hash (token_hash) | 동일 refresh token 중복 저장 방지(UNIQUE). refresh 요청 시 token_hash로 단건 조회 최적화
idx_refresh_tokens_member_id (member_id) | 사용자 전체 토큰 폐기/정리 시 범위 조회 최적화

## 5) 운영 정책
- refresh token 원문은 저장하지 않고 SHA-256 해시(`token_hash`)만 저장
- refresh 재발급 시 기존 토큰은 revoked_at 기록
- 폐기된 토큰 재사용 감지 시 해당 사용자 전체 토큰 폐기
- 만료 토큰은 배치로 삭제
- 로그아웃 시 사용자 전체 토큰 폐기
- 만료 토큰 정리 배치는 매일 04:00(서버 타임존 기준) 실행
