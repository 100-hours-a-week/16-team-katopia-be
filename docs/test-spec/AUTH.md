# 인증/토큰(Auth)

## 토큰 재발급/로그아웃
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-AUTH-S-01 | RT로 토큰 재발급 성공 | 유효 RT 쿠키 | POST /api/auth/tokens | AT/RT 재발급 응답 |
| TC-AUTH-S-02 | 로그아웃 성공 | 인증 상태 | DELETE /api/auth/tokens | RT 쿠키 만료 응답 |
| TC-AUTH-S-03 | 회원가입 완료 시 등록 쿠키 만료 | 유효 등록 토큰 | 회원가입 완료 | 등록 쿠키 maxAge=0 |
| TC-AUTH-F-01 | 토큰 재발급 실패(RT 없음) | RT 쿠키 없음 | POST /api/auth/tokens | 401 오류 반환 |
| TC-AUTH-F-02 | 토큰 재발급 실패(RT 만료/위조) | 유효하지 않은 RT | POST /api/auth/tokens | 401 오류 반환 |

## AuthTokenService 실패(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-AUTH-SVC-F-01 | 토큰 재발급 실패(토큰 파싱 실패) | refreshToken에서 memberId null | refreshTokens 호출 | AUTH-E-013 반환 |
| TC-AUTH-SVC-F-02 | 토큰 재발급 실패(미등록 토큰) | hash 미존재 | refreshTokens 호출 | AUTH-E-013 반환 |
| TC-AUTH-SVC-F-03 | 토큰 재발급 실패(폐기/만료) | revoked/expired RT | refreshTokens 호출 | AUTH-E-013 반환 + revokeAll 호출 |
| TC-AUTH-SVC-F-04 | 토큰 재발급 실패(탈퇴 회원) | WITHDRAWN 회원 | refreshTokens 호출 | MEMBER-E-050 반환 |

## 가입 필터(RegistrationTokenFilter)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-REG-FILTER-S-01 | 등록 요청 쿠키 정상 처리 | 유효 등록 쿠키 | POST /api/members | request attribute 설정 |
| TC-REG-FILTER-F-01 | 등록 쿠키로 리프레시 요청 차단 | registration_token 존재 | POST /api/auth/tokens | AUTH-E-001 반환 |
| TC-REG-FILTER-F-02 | 등록 요청 쿠키 누락 | registration_token 없음 | POST /api/members | AUTH-E-010 반환 |

## 가입 필터 통합(WebMvcTest)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-REG-FILTER-INT-S-01 | 닉네임 중복 체크는 쿠키 유지 | registration_token 존재 | GET /api/members/check | 200 + Set-Cookie 없음 |
| TC-REG-FILTER-INT-S-02 | 회원가입 요청은 쿠키로만 통과 | registration_token 존재 | POST /api/members | 200 + Set-Cookie 없음 |
| TC-REG-FILTER-INT-F-01 | 허가되지 않은 경로 요청 시 쿠키 만료 | registration_token 존재 | GET /api/posts | 401 + Set-Cookie 만료 |
