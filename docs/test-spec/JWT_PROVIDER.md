# JWT Provider

| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-JWT-S-01 | 유효 토큰에서 memberId 추출 성공 | 유효 Access 토큰 | extractMemberId | memberId 반환 |
| TC-JWT-S-02 | Bearer 헤더 추출 성공 | Authorization: Bearer {token} | extractBearerToken | token 반환 |
| TC-JWT-S-03 | 쿠키 값 추출 성공 | refresh_token 존재 | extractCookieValue | 값 반환 |
| TC-JWT-S-04 | Access 토큰 발급 성공 | memberId 존재 | createAccessToken | typ=access, 만료시간 존재 |
| TC-JWT-S-05 | Refresh 토큰 발급 성공 | memberId 존재 | createRefreshToken | typ=refresh, 만료시간 존재 |
| TC-JWT-S-06 | Registration 토큰 발급 성공 | memberId 존재 | createRegistrationToken | typ=registration, 만료시간 존재 |
| TC-JWT-S-07 | AT/RT 동시 발급 성공 | memberId 존재 | issueTokens | access/refresh 반환 |
| TC-JWT-S-08 | Refresh 쿠키 생성 | refresh token | buildRefreshCookie | name/path/maxAge 확인 |
| TC-JWT-S-09 | Registration 쿠키 생성 | registration token | buildRegistrationCookie | name/path/maxAge 확인 |
| TC-JWT-S-10 | Refresh 쿠키 삭제 | - | clearRefreshCookie | maxAge=0 확인 |
| TC-JWT-S-11 | Registration 쿠키 삭제 | - | clearRegistrationCookie | maxAge=0 확인 |
| TC-JWT-F-01 | 만료 토큰 검증 실패 | 만료된 Access 토큰 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-F-02 | 서명 불일치 토큰 실패 | 다른 secret으로 서명된 토큰 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-F-03 | 토큰 타입 불일치 실패 | Access 토큰 | extractMemberId(REFRESH) | null 반환 |
| TC-JWT-F-04 | typ 누락 토큰 실패 | typ 없는 토큰 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-F-05 | memberId 누락 토큰 실패 | memberId 없는 토큰 | extractMemberId | null 반환 |
| TC-JWT-F-06 | 잘못된 형식 토큰 실패 | 임의 문자열 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-F-07 | Bearer 헤더 누락/형식 오류 | Authorization 없음/Basic | extractBearerToken | null 반환 |
| TC-JWT-F-08 | 쿠키 값 추출 실패 | refresh_token 미존재 | extractCookieValue | null 반환 |
