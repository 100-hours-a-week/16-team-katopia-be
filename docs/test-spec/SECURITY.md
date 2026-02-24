# 보안/예외(Security)

## 보안/예외 시나리오
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-SEC-F-01 | 인증 필요 API 접근 차단 | 인증 없음 | 보호 리소스 접근 | 401 응답 |
| TC-SEC-F-02 | 권한 없는 리소스 접근 차단 | 타인 리소스 | 수정/삭제 요청 | 403 응답 |
| TC-ERR-F-01 | 존재하지 않는 경로 처리 | 미존재 경로 | 요청 | 404 응답 |
| TC-ERR-F-02 | 메서드 미지원 처리 | 지원하지 않는 HTTP 메서드 | 요청 | 405 응답 |

## SecuritySupport(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-SEC-SUPPORT-S-01 | 인증 principal이 있으면 memberId 반환 | principal=MemberPrincipal | requireMemberId 호출 | memberId 반환 |
| TC-SEC-SUPPORT-S-02 | null principal이면 null 반환 | principal=null | findMemberIdOrNull 호출 | null 반환 |
| TC-SEC-SUPPORT-S-03 | principal이 있으면 memberId 반환 | principal=MemberPrincipal | findMemberIdOrNull 호출 | memberId 반환 |
| TC-SEC-SUPPORT-F-01 | 인증 principal이 없으면 예외 | principal=null | requireMemberId 호출 | NOT_FOUND_AT 반환 |
