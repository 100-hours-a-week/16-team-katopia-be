# Refresh Token

## RefreshToken 도메인(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-REFRESH-S-01 | revoke 수행 시 revokedAt 설정 | 유효 토큰 | revoke 호출 | revokedAt 설정 |
| TC-REFRESH-S-02 | revoke 재호출 시 상태 유지 | revoked 토큰 | revoke 호출 | revokedAt 변경 없음 |
