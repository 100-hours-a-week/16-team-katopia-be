# Test Scenario Specification

- **테스트 목적**: 핵심 비즈니스 규칙의 정상 동작 및 예외 처리를 검증한다.
- **테스트 관점**: 정상 흐름과 실패 흐름을 분리한다.
- **책임 범위**: 서비스/도메인 로직 및 보안 유틸(예: JwtProvider) 중심. 외부 연동(소셜 OAuth2, 인프라, S3 등)과 실제 DB 영속성은 제외한다.
- **실행 원칙**: 외부 시스템은 Mock/Stub. 테스트는 독립 실행, 종료 후 상태 초기화.

## 상태 표시
- ⬜ 미작성
- 🟡 작성 중
- ✅ 완료
- 🔴 보류/제외

## 우선순위
- High: 핵심 플로우/치명적 장애로 직결
- Medium: 주요 기능이지만 대체 가능/부분 장애
- Low: 부가 기능/경계 확인

## 범위/제외
- **포함**: 회원/게시글/댓글/검색/토큰 재발급·로그아웃 비즈니스 로직, JwtProvider 단위 테스트
- **제외**: OAuth2 로그인 리디렉션/핸들러, DB 스키마/인덱스 검증, 외부 API, 실제 보안 필터 체인

## 테스트 시나리오

### 1) 회원(Member)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-MEMBER-01 | ⬜ | Medium | 닉네임 사용 가능 여부(가능) | 동일 닉네임이 없음 | 닉네임 사용 가능 여부 확인 요청 | isAvailable=true 반환 |
| TC-MEMBER-02 | ⬜ | Medium | 닉네임 사용 가능 여부(불가) | 동일 닉네임이 존재 | 닉네임 사용 가능 여부 확인 요청 | isAvailable=false 반환 |
| TC-MEMBER-03 | ⬜ | High | 회원가입 성공 | 유효한 등록 토큰, 유효 닉네임 | 회원가입 요청 | ACTIVE 전환 + AT/RT 발급 |
| TC-MEMBER-04 | ⬜ | Medium | 회원가입 실패(닉네임 형식 오류) | 유효 등록 토큰 | 닉네임 규칙 위반 | 400 오류 반환 |
| TC-MEMBER-05 | ⬜ | Medium | 회원가입 실패(중복 닉네임) | 유효 등록 토큰, 중복 닉네임 | 회원가입 요청 | 409 오류 반환 |
| TC-MEMBER-06 | ⬜ | High | 공개 프로필 조회 성공 | ACTIVE 회원 존재 | /api/members/{id} 조회 | 프로필 응답 반환 |
| TC-MEMBER-06-1 | ⬜ | Medium | 공개 프로필 조회 실패(PENDING) | PENDING 회원 존재 | /api/members/{id} 조회 | 404 오류 반환 |
| TC-MEMBER-06-2 | ⬜ | Medium | 공개 프로필 조회 실패(WITHDRAWN) | WITHDRAWN 회원 존재 | /api/members/{id} 조회 | 404 오류 반환 |
| TC-MEMBER-07 | ⬜ | High | 내 프로필 조회 성공 | 인증된 사용자 | /api/members/me 조회 | 상세 프로필 반환 |
| TC-MEMBER-08 | ⬜ | High | 프로필 수정 성공 | 인증된 사용자, 유효 요청 | 프로필 수정 | 반영된 프로필 반환 |
| TC-MEMBER-09 | ⬜ | Medium | 프로필 수정 실패(닉네임 중복) | 인증된 사용자, 중복 닉네임 | 프로필 수정 | 409 오류 반환 |
| TC-MEMBER-10 | ⬜ | High | 회원 탈퇴 성공 | 인증된 사용자 | 탈퇴 요청 | 204 응답 |

#### 1-1) 닉네임 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-NICK-01 | ✅ | High | 필수값 누락(null) | nickname=null | 검증 수행 | COMMON-E-001 반환 |
| TC-NICK-02 | ✅ | High | 필수값 누락(빈 문자열) | nickname="" | 검증 수행 | COMMON-E-001 반환 |
| TC-NICK-03 | ✅ | Medium | 공백 포함 실패 | nickname="ab cd" | 검증 수행 | MEMBER-E-003 반환 |
| TC-NICK-04 | ✅ | Medium | 길이 하한 위반 | nickname="a" | 검증 수행 | MEMBER-E-001 반환 |
| TC-NICK-05 | ✅ | Medium | 길이 상한 위반 | nickname=21자 | 검증 수행 | MEMBER-E-001 반환 |
| TC-NICK-06 | ✅ | Medium | 허용되지 않는 문자 포함 | nickname="ab!" | 검증 수행 | MEMBER-E-002 반환 |
| TC-NICK-07 | ✅ | Medium | 닉네임 유효성 성공 | nickname="user_01" | 검증 수행 | 오류 없음 |

#### 1-2) 성별 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-GENDER-01 | ✅ | High | 필수값 누락(null) | gender=null, required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-GENDER-02 | ✅ | High | 필수값 누락(빈 문자열) | gender="", required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-GENDER-03 | ✅ | Medium | 유효하지 않은 값 | gender="X" | 검증 수행 | MEMBER-E-020 반환 |
| TC-GENDER-04 | ✅ | Medium | 유효성 성공(대문자) | gender="M" | 검증 수행 | 오류 없음 |
| TC-GENDER-05 | ✅ | Medium | 유효성 성공(소문자) | gender="f" | 검증 수행 | 오류 없음 |

#### 1-3) 키 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-HEIGHT-01 | ✅ | High | 필수값 누락(null) | height=null, required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-HEIGHT-02 | ✅ | High | 필수값 누락(빈 문자열) | height="", required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-HEIGHT-03 | ✅ | Medium | 숫자 형식 오류 | height="17a" | 검증 수행 | MEMBER-E-021 반환 |
| TC-HEIGHT-04 | ✅ | Medium | 범위 하한 위반 | height="49" | 검증 수행 | MEMBER-E-022 반환 |
| TC-HEIGHT-05 | ✅ | Medium | 범위 상한 위반 | height="301" | 검증 수행 | MEMBER-E-022 반환 |
| TC-HEIGHT-06 | ✅ | Medium | 유효성 성공 | height="170" | 검증 수행 | 오류 없음 |

#### 1-4) 몸무게 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-WEIGHT-01 | ✅ | High | 필수값 누락(null) | weight=null, required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-WEIGHT-02 | ✅ | High | 필수값 누락(빈 문자열) | weight="", required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-WEIGHT-03 | ✅ | Medium | 숫자 형식 오류 | weight="7a" | 검증 수행 | MEMBER-E-023 반환 |
| TC-WEIGHT-04 | ✅ | Medium | 범위 하한 위반 | weight="19" | 검증 수행 | MEMBER-E-024 반환 |
| TC-WEIGHT-05 | ✅ | Medium | 범위 상한 위반 | weight="501" | 검증 수행 | MEMBER-E-024 반환 |
| TC-WEIGHT-06 | ✅ | Medium | 유효성 성공 | weight="70" | 검증 수행 | 오류 없음 |

#### 1-5) 스타일 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-STYLE-01 | ✅ | Medium | 스타일 없음(null) | style=null | 검증 수행 | 오류 없음 |
| TC-STYLE-02 | ✅ | Medium | 스타일 빈 리스트 | style=[] | 검증 수행 | 오류 없음 |
| TC-STYLE-03 | ✅ | Medium | 스타일 개수 초과 | style=3개 | 검증 수행 | MEMBER-E-031 반환 |
| TC-STYLE-04 | ✅ | Medium | 공백 포함 스타일 실패 | style=[" "] | 검증 수행 | MEMBER-E-030 반환 |
| TC-STYLE-05 | ✅ | Medium | 유효하지 않은 값 | style=["UNKNOWN"] | 검증 수행 | MEMBER-E-030 반환 |
| TC-STYLE-06 | ✅ | Medium | 유효성 성공 | style=["MINIMAL","CASUAL"] | 검증 수행 | 오류 없음 |

### 2) 게시글(Post)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-POST-01 | ⬜ | High | 게시글 작성 성공 | 인증된 사용자, 유효 본문/이미지/태그 | 게시글 작성 | 201 응답 |
| TC-POST-02 | ⬜ | Medium | 게시글 작성 실패(본문 없음) | 인증된 사용자 | 빈 본문 | 400 오류 반환 |
| TC-POST-03 | ⬜ | Medium | 게시글 작성 실패(본문 200자 초과) | 인증된 사용자 | 긴 본문 | 400 오류 반환 |
| TC-POST-04 | ⬜ | Medium | 게시글 작성 실패(이미지 수량 오류) | 인증된 사용자 | 이미지 0장/4장 | 400 오류 반환 |
| TC-POST-05 | ⬜ | Medium | 게시글 작성 실패(태그 길이/개수 오류) | 인증된 사용자 | 태그 길이 또는 개수 위반 | 400 오류 반환 |
| TC-POST-06 | ⬜ | High | 게시글 목록 조회(커서) | 인증된 사용자, 게시글 존재 | size/after로 조회 | 최신순 목록 + nextCursor |
| TC-POST-07 | ⬜ | High | 게시글 상세 조회 성공 | 게시글 존재 | 상세 조회 | 상세 응답 반환 |
| TC-POST-08 | ⬜ | High | 게시글 수정 성공 | 작성자 본인 | 유효 수정 요청 | 수정 응답 반환 |
| TC-POST-09 | ⬜ | High | 게시글 수정 실패(타인) | 작성자 아님 | 수정 요청 | 403 오류 반환 |
| TC-POST-10 | ⬜ | High | 게시글 삭제 성공 | 작성자 본인 | 삭제 요청 | 204 응답 |
| TC-POST-11 | ⬜ | High | 게시글 삭제 실패(타인) | 작성자 아님 | 삭제 요청 | 403 오류 반환 |
| TC-POST-12 | ⬜ | Medium | 게시글 좋아요 성공 | 인증된 사용자 | 좋아요 요청 | 201 응답 |
| TC-POST-13 | ⬜ | Medium | 게시글 좋아요 실패(중복) | 이미 좋아요 상태 | 좋아요 요청 | 409 오류 반환 |
| TC-POST-14 | ⬜ | Medium | 게시글 좋아요 해제 성공 | 좋아요 상태 | 해제 요청 | 204 응답 |
| TC-POST-15 | ⬜ | Medium | 게시글 좋아요 해제 실패(기록 없음) | 미좋아요 상태 | 해제 요청 | 404 오류 반환 |

### 3) 댓글(Comment)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-COMMENT-01 | ⬜ | High | 댓글 작성 성공 | 인증된 사용자, 게시글 존재 | 댓글 작성 | 201 응답 |
| TC-COMMENT-02 | ⬜ | Medium | 댓글 작성 실패(본문 없음) | 인증된 사용자 | 빈 본문 | 400 오류 반환 |
| TC-COMMENT-03 | ⬜ | Medium | 댓글 작성 실패(본문 200자 초과) | 인증된 사용자 | 긴 본문 | 400 오류 반환 |
| TC-COMMENT-04 | ⬜ | High | 댓글 목록 조회(커서) | 댓글 존재 | size/after로 조회 | 최신순 목록 + nextCursor |
| TC-COMMENT-05 | ⬜ | High | 댓글 수정 성공 | 작성자 본인 | 수정 요청 | 200 응답 |
| TC-COMMENT-06 | ⬜ | High | 댓글 수정 실패(타인) | 작성자 아님 | 수정 요청 | 403 오류 반환 |
| TC-COMMENT-07 | ⬜ | High | 댓글 삭제 성공 | 작성자 본인 | 삭제 요청 | 204 응답 |
| TC-COMMENT-08 | ⬜ | High | 댓글 삭제 실패(타인) | 작성자 아님 | 삭제 요청 | 403 오류 반환 |
| TC-COMMENT-09 | ⬜ | Medium | 댓글 수정 실패(댓글 없음) | 댓글 미존재 | 수정 요청 | 404 오류 반환 |
| TC-COMMENT-10 | ⬜ | Medium | 댓글 삭제 실패(댓글 없음) | 댓글 미존재 | 삭제 요청 | 404 오류 반환 |

### 4) 검색(Search)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-SEARCH-01 | ⬜ | High | 사용자 검색 성공(query prefix) | 인증된 사용자, ACTIVE 사용자 존재 | /api/search/users?query=... | 최신순 사용자 목록 + nextCursor |
| TC-SEARCH-02 | ⬜ | Medium | 사용자 검색 실패(검색어 길이) | 검색어 길이<2 or >100 | 검색 요청 | 400 오류 반환 |
| TC-SEARCH-03 | ⬜ | Medium | 사용자 검색 실패(인증 없음) | 인증 없음 | 검색 요청 | 401 오류 반환 |
| TC-SEARCH-04 | ⬜ | High | 게시글 검색 성공(본문) | 본문에 매칭되는 글 존재 | /api/search/posts?query=... | 본문 매칭 결과 + nextCursor |
| TC-SEARCH-05 | ⬜ | High | 게시글 검색 성공(태그) | 태그에 매칭되는 글 존재 | /api/search/posts?query=#tag | 태그 매칭 결과 + nextCursor |
| TC-SEARCH-06 | ⬜ | Medium | 게시글 검색 실패(검색어 길이) | 검색어 길이<2 or >100 | 검색 요청 | 400 오류 반환 |
| TC-SEARCH-07 | ⬜ | Medium | 게시글 검색 실패(인증 없음) | 인증 없음 | 검색 요청 | 401 오류 반환 |
| TC-SEARCH-08 | ⬜ | Medium | 커서/페이지 사이즈 검증 | after/size 형식 오류 | 검색 요청 | 400 오류 반환 |

### 5) 토큰 재발급/로그아웃(Auth)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-AUTH-01 | ⬜ | High | RT로 토큰 재발급 성공 | 유효 RT 쿠키 | POST /api/auth/tokens | AT/RT 재발급 응답 |
| TC-AUTH-02 | ⬜ | Medium | 토큰 재발급 실패(RT 없음) | RT 쿠키 없음 | POST /api/auth/tokens | 401 오류 반환 |
| TC-AUTH-03 | ⬜ | Medium | 토큰 재발급 실패(RT 만료/위조) | 유효하지 않은 RT | POST /api/auth/tokens | 401 오류 반환 |
| TC-AUTH-04 | ⬜ | Medium | 로그아웃 성공 | 인증 상태 | DELETE /api/auth/tokens | RT 쿠키 만료 응답 |
| TC-AUTH-05 | ✅ | Medium | 회원가입 완료 시 등록 쿠키 만료 | 유효 등록 토큰 | 회원가입 완료 | 등록 쿠키 maxAge=0 |

### 6) JWT Provider (Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-JWT-01 | ✅ | High | 유효 토큰에서 memberId 추출 성공 | 유효 Access 토큰 | extractMemberId | memberId 반환 |
| TC-JWT-02 | ✅ | Medium | 만료 토큰 검증 실패 | 만료된 Access 토큰 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-03 | ✅ | Medium | 서명 불일치 토큰 실패 | 다른 secret으로 서명된 토큰 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-04 | ✅ | Medium | 토큰 타입 불일치 실패 | Access 토큰 | extractMemberId(REFRESH) | null 반환 |
| TC-JWT-05 | ✅ | Medium | typ 누락 토큰 실패 | typ 없는 토큰 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-06 | ✅ | Medium | memberId 누락 토큰 실패 | memberId 없는 토큰 | extractMemberId | null 반환 |
| TC-JWT-07 | ✅ | Medium | 잘못된 형식 토큰 실패 | 임의 문자열 | isTokenType/extractMemberId | null 반환 |
| TC-JWT-08 | ✅ | Low | Bearer 헤더 추출 성공 | Authorization: Bearer {token} | extractBearerToken | token 반환 |
| TC-JWT-09 | ✅ | Low | Bearer 헤더 누락/형식 오류 | Authorization 없음/Basic | extractBearerToken | null 반환 |
| TC-JWT-10 | ✅ | Low | 쿠키 값 추출 성공/실패 | refresh_token 존재/미존재 | extractCookieValue | 값/ null 반환 |
| TC-JWT-11 | ✅ | Medium | Access 토큰 발급 성공 | memberId 존재 | createAccessToken | typ=access, 만료시간 존재 |
| TC-JWT-12 | ✅ | Medium | Refresh 토큰 발급 성공 | memberId 존재 | createRefreshToken | typ=refresh, 만료시간 존재 |
| TC-JWT-13 | ✅ | Medium | Registration 토큰 발급 성공 | memberId 존재 | createRegistrationToken | typ=registration, 만료시간 존재 |
| TC-JWT-14 | ✅ | Medium | AT/RT 동시 발급 성공 | memberId 존재 | issueTokens | access/refresh 반환 |
| TC-JWT-15 | ✅ | Low | Refresh 쿠키 생성 | refresh token | buildRefreshCookie | name/path/maxAge 확인 |
| TC-JWT-16 | ✅ | Low | Registration 쿠키 생성 | registration token | buildRegistrationCookie | name/path/maxAge 확인 |
| TC-JWT-17 | ✅ | Low | Refresh 쿠키 삭제 | - | clearRefreshCookie | maxAge=0 확인 |
| TC-JWT-18 | ✅ | Low | Registration 쿠키 삭제 | - | clearRegistrationCookie | maxAge=0 확인 |

### 7) Presign (Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-PRESIGN-01 | ✅ | Medium | 확장자 정규화 | extension=\".PNG\" | createPresignedUrls | contentType=image/png |
| TC-PRESIGN-02 | ✅ | Medium | 확장자 누락 실패 | extension=null | createPresignedUrls | COMMON-E-007 반환 |
| TC-PRESIGN-03 | ✅ | Medium | cloudfrontBaseUrl 누락 | cloudfrontBaseUrl=null | createPresignedUrls | COMMON-E-007 반환 |
| TC-PRESIGN-04 | ✅ | Medium | 버킷 누락 | bucket=null | createPresignedUrls | COMMON-E-007 반환 |
| TC-PRESIGN-05 | ✅ | Medium | maxSize 초과 | maxSizeBytes>30MB | createPresignedUrls | COMMON-E-007 반환 |
| TC-PRESIGN-06 | ✅ | Medium | 업로드/접근 URL 생성 | 유효 설정 | createPresignedUrls | uploadUrl/accessUrl 반환 |
| TC-PRESIGN-07 | ✅ | Low | contentType 매핑 | extension=\".PNG\" | createPresignedUrls | contentType=image/png |

#### 7-1) Presign 요청 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-PRESIGN-VAL-01 | ✅ | Medium | 요청 null | request=null | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-02 | ✅ | Medium | category 누락 | category=null | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-03 | ✅ | Medium | extensions 누락 | extensions=null | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-04 | ✅ | Medium | extensions 빈 리스트 | extensions=[] | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-05 | ✅ | Medium | 개수 초과 | size>maxCount | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-06 | ✅ | Medium | 허용되지 않는 확장자 | extensions=[\"exe\"] | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-07 | ✅ | Low | 확장자 정규화 허용 | extensions=[\".PNG\"] | validate | 오류 없음 |
| TC-PRESIGN-VAL-08 | ✅ | Low | 최대 개수 허용 | category=POST, size=max | validate | 오류 없음 |

### 8) 댓글 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-COMMENT-VAL-01 | ✅ | Medium | 본문 누락(null) | content=null | 검증 수행 | COMMENT-E-001 반환 |
| TC-COMMENT-VAL-02 | ✅ | Medium | 본문 누락(빈 문자열) | content=\"\" | 검증 수행 | COMMENT-E-001 반환 |
| TC-COMMENT-VAL-03 | ✅ | Medium | 본문 공백만 | content=\"   \" | 검증 수행 | COMMENT-E-001 반환 |
| TC-COMMENT-VAL-04 | ✅ | Medium | 본문 길이 초과 | content=201자 | 검증 수행 | COMMENT-E-002 반환 |
| TC-COMMENT-VAL-05 | ✅ | Medium | 본문 유효성 성공 | content=유효문자열 | 검증 수행 | 오류 없음 |

### 9) 게시글 본문 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-POST-CONTENT-01 | ✅ | Medium | 본문 누락(null) | content=null | 검증 수행 | POST-E-000 반환 |
| TC-POST-CONTENT-02 | ✅ | Medium | 본문 누락(빈 문자열) | content=\"\" | 검증 수행 | POST-E-000 반환 |
| TC-POST-CONTENT-03 | ✅ | Medium | 본문 공백만 | content=\"   \" | 검증 수행 | POST-E-000 반환 |
| TC-POST-CONTENT-04 | ✅ | Medium | 본문 길이 초과 | content=201자 | 검증 수행 | POST-E-001 반환 |
| TC-POST-CONTENT-05 | ✅ | Medium | 본문 유효성 성공 | content=유효문자열 | 검증 수행 | 오류 없음 |

### 10) 게시글 태그 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-POST-TAG-01 | ✅ | Medium | 태그 null | tags=null | 검증 수행 | 오류 없음 |
| TC-POST-TAG-02 | ✅ | Medium | 태그 빈 리스트 | tags=[] | 검증 수행 | 오류 없음 |
| TC-POST-TAG-03 | ✅ | Medium | 태그 개수 초과 | tags=11개 | 검증 수행 | POST-E-021 반환 |
| TC-POST-TAG-04 | ✅ | Medium | 태그 공백 실패 | tags=[\" \"] | 검증 수행 | POST-E-020 반환 |
| TC-POST-TAG-05 | ✅ | Medium | 태그 길이 위반 | tags=[\"a\"*21] | 검증 수행 | POST-E-020 반환 |
| TC-POST-TAG-06 | ✅ | Medium | 태그 유효성 성공 | tags=[\"DAILY\",\"MINIMAL\"] | 검증 수행 | 오류 없음 |

### 11) 이미지 URL 유효성(Unit)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-IMAGE-01 | ✅ | Medium | 이미지 리스트 null | imageUrls=null | 검증 수행 | POST-E-010 반환 |
| TC-IMAGE-02 | ✅ | Medium | 이미지 리스트 빈 값 | imageUrls=[] | 검증 수행 | POST-E-010 반환 |
| TC-IMAGE-03 | ✅ | Medium | 이미지 개수 초과 | imageUrls=4개 | 검증 수행 | POST-E-010 반환 |
| TC-IMAGE-04 | ✅ | Medium | 이미지 URL 공백 | imageUrls=[\" \"] | 검증 수행 | POST-E-010 반환 |
| TC-IMAGE-05 | ✅ | Medium | 이미지 유효성 성공 | imageUrls=1~3개 | 검증 수행 | 오류 없음 |

### 12) 가입 필터(RegistrationTokenFilter)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-REG-FILTER-01 | ✅ | Medium | 등록 쿠키로 리프레시 요청 차단 | registration_token 존재 | POST /api/auth/tokens | AUTH-E-001 반환 |
| TC-REG-FILTER-02 | ✅ | Medium | 등록 요청 쿠키 누락 | registration_token 없음 | POST /api/members | AUTH-E-010 반환 |
| TC-REG-FILTER-03 | ✅ | Medium | 등록 요청 쿠키 정상 처리 | 유효 등록 쿠키 | POST /api/members | request attribute 설정 |

### 13) 보안/예외(경계)
| TC ID | 상태 | 우선순위 | 설명 | GIVEN | WHEN | THEN |
|---|---|---|---|---|---|---|
| TC-SEC-01 | 🔴 | High | 인증 필요 API 접근 차단 | 인증 없음 | 보호 리소스 접근 | 401 응답 |
| TC-SEC-02 | 🔴 | High | 권한 없는 리소스 접근 차단 | 타인 리소스 | 수정/삭제 요청 | 403 응답 |
| TC-ERR-01 | 🔴 | Medium | 존재하지 않는 경로 처리 | 미존재 경로 | 요청 | 404 응답 |
| TC-ERR-02 | 🔴 | Medium | 메서드 미지원 처리 | 지원하지 않는 HTTP 메서드 | 요청 | 405 응답 |
