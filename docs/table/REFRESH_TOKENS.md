# Refresh Tokens

## 1. 테이블 설명
JWT 기반 인증에서 Refresh Token Rotation(RTR) 적용과 RT의 유효/폐기 상태를 저장하는 테이블이다.

### 연관 관계
- members(1) : refresh_tokens(N)

## 2. 기능적 역할
- refresh token 유효성 판단(만료/폐기)
- RTR에서 사용된 토큰 폐기 + 폐기 토큰 재사용 차단
- 만료 토큰 정리(배치)
- 재사용 감지 시 사용자 전체 토큰 폐기

## 3. 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | id | BIGINT | pk, nn | 토큰 식별자
2 | member_id | BIGINT | nn | 토큰 소유자
3 | token_hash | VARCHAR(64) | unique, nn | refresh token 해시
4 | created_at | DATETIME | nn | 발급 시각
5 | expires_at | DATETIME | nn | 만료 시각
6 | revoked_at | DATETIME |  | 폐기 시각

## 4. 인덱스/제약
| 인덱스/제약 | 근거 |
| --- | --- |
| `uk_refresh_tokens_token_hash (token_hash)` | 동일 refresh token 해시의 중복 저장을 막고, 재발급/재사용 감지 시 단건 조회를 빠르게 하기 위한 유니크 제약 |
| `idx_refresh_tokens_member_id (member_id)` | 사용자 단위 토큰 폐기, 로그아웃, 재사용 감지 후 전체 정리 범위를 빠르게 조회하기 위한 인덱스 |

## 5. 운영 정책
- refresh token 원문은 저장하지 않고 SHA-256 해시(token_hash)만 저장한다.
- refresh 재발급 시 기존 토큰은 revoked_at 기록한다.
- 폐기된 토큰 재사용 감지 시 해당 사용자 전체 토큰을 폐기한다.
- 만료 토큰은 배치로 삭제한다.
- 로그아웃 시 사용자 전체 토큰을 폐기한다.
- 만료 토큰 정리 배치는 매일 04:00(서버 타임존 기준) 실행한다.
