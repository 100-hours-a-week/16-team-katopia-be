# 회원(Member)

| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-MEMBER-S-01 | 닉네임 사용 가능 여부(가능) | 동일 닉네임이 없음 | 닉네임 사용 가능 여부 확인 요청 | isAvailable=true 반환 |
| TC-MEMBER-S-02 | 회원가입 성공 | 유효한 등록 토큰, 유효 닉네임 | 회원가입 요청 | ACTIVE 전환 + AT/RT 발급 |
| TC-MEMBER-S-03 | 공개 프로필 조회 성공 | ACTIVE 회원 존재 | /api/members/{id} 조회 | 프로필 응답 반환 |
| TC-MEMBER-S-04 | 내 프로필 조회 성공 | 인증된 사용자 | /api/members/me 조회 | 상세 프로필 반환 |
| TC-MEMBER-S-05 | 프로필 수정 성공 | 인증된 사용자, 유효 요청 | 프로필 수정 | 반영된 프로필 반환 |
| TC-MEMBER-S-06 | 회원 탈퇴 성공 | 인증된 사용자 | 탈퇴 요청 | 204 응답 |
| TC-MEMBER-F-01 | 닉네임 사용 가능 여부(불가) | 동일 닉네임이 존재 | 닉네임 사용 가능 여부 확인 요청 | isAvailable=false 반환 |
| TC-MEMBER-F-02 | 회원가입 실패(닉네임 형식 오류) | 유효 등록 토큰 | 닉네임 규칙 위반 | 400 오류 반환 |
| TC-MEMBER-F-03 | 회원가입 실패(중복 닉네임) | 유효 등록 토큰, 중복 닉네임 | 회원가입 요청 | 409 오류 반환 |
| TC-MEMBER-F-04 | 공개 프로필 조회 실패(PENDING) | PENDING 회원 존재 | /api/members/{id} 조회 | 404 오류 반환 |
| TC-MEMBER-F-05 | 공개 프로필 조회 실패(WITHDRAWN) | WITHDRAWN 회원 존재 | /api/members/{id} 조회 | 404 오류 반환 |
| TC-MEMBER-F-06 | 프로필 수정 실패(닉네임 중복) | 인증된 사용자, 중복 닉네임 | 프로필 수정 | 409 오류 반환 |

## 닉네임 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-NICK-S-01 | 닉네임 유효성 성공 | nickname="user_01" | 검증 수행 | 오류 없음 |
| TC-NICK-F-01 | 필수값 누락(null) | nickname=null | 검증 수행 | COMMON-E-001 반환 |
| TC-NICK-F-02 | 필수값 누락(빈 문자열) | nickname="" | 검증 수행 | COMMON-E-001 반환 |
| TC-NICK-F-03 | 공백 포함 실패 | nickname="ab cd" | 검증 수행 | MEMBER-E-003 반환 |
| TC-NICK-F-04 | 길이 하한 위반 | nickname="a" | 검증 수행 | MEMBER-E-001 반환 |
| TC-NICK-F-05 | 길이 상한 위반 | nickname=21자 | 검증 수행 | MEMBER-E-001 반환 |
| TC-NICK-F-06 | 허용되지 않는 문자 포함 | nickname="ab!" | 검증 수행 | MEMBER-E-002 반환 |

## 성별 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-GENDER-S-01 | 유효성 성공(대문자) | gender="M" | 검증 수행 | 오류 없음 |
| TC-GENDER-F-01 | 필수값 누락(null) | gender=null, required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-GENDER-F-02 | 필수값 누락(빈 문자열) | gender="", required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-GENDER-F-03 | 유효하지 않은 값 | gender="X" | 검증 수행 | MEMBER-E-020 반환 |
| TC-GENDER-F-04 | 유효하지 않은 값(소문자) | gender="f" | 검증 수행 | MEMBER-E-020 반환 |

## 키 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-HEIGHT-S-01 | 유효성 성공 | height="170" | 검증 수행 | 오류 없음 |
| TC-HEIGHT-F-01 | 필수값 누락(null) | height=null, required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-HEIGHT-F-02 | 필수값 누락(빈 문자열) | height="", required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-HEIGHT-F-03 | 숫자 형식 오류 | height="17a" | 검증 수행 | MEMBER-E-021 반환 |
| TC-HEIGHT-F-04 | 범위 하한 위반 | height="49" | 검증 수행 | MEMBER-E-022 반환 |
| TC-HEIGHT-F-05 | 범위 상한 위반 | height="301" | 검증 수행 | MEMBER-E-022 반환 |

## 몸무게 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-WEIGHT-S-01 | 유효성 성공 | weight="70" | 검증 수행 | 오류 없음 |
| TC-WEIGHT-F-01 | 필수값 누락(null) | weight=null, required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-WEIGHT-F-02 | 필수값 누락(빈 문자열) | weight="", required=true | 검증 수행 | COMMON-E-001 반환 |
| TC-WEIGHT-F-03 | 숫자 형식 오류 | weight="7a" | 검증 수행 | MEMBER-E-023 반환 |
| TC-WEIGHT-F-04 | 범위 하한 위반 | weight="19" | 검증 수행 | MEMBER-E-024 반환 |
| TC-WEIGHT-F-05 | 범위 상한 위반 | weight="501" | 검증 수행 | MEMBER-E-024 반환 |

## 스타일 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-STYLE-S-01 | 스타일 없음(null) | style=null | 검증 수행 | 오류 없음 |
| TC-STYLE-S-02 | 스타일 빈 리스트 | style=[] | 검증 수행 | 오류 없음 |
| TC-STYLE-S-03 | 유효성 성공 | style=["MINIMAL","CASUAL"] | 검증 수행 | 오류 없음 |
| TC-STYLE-F-01 | 스타일 개수 초과 | style=3개 | 검증 수행 | MEMBER-E-031 반환 |
| TC-STYLE-F-02 | 공백 포함 스타일 실패 | style=[" "] | 검증 수행 | MEMBER-E-030 반환 |
| TC-STYLE-F-03 | 유효하지 않은 값 | style=["UNKNOWN"] | 검증 수행 | MEMBER-E-030 반환 |

## 회원가입/프로필 서비스(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-MEMBER-REG-S-01 | 회원가입 성공(선택 필드 입력) | 선택값 모두 제공 | signup 호출 | 프로필/스타일 반영 |
| TC-MEMBER-REG-S-02 | 회원가입 성공(선택값 누락) | 선택값 null | signup 호출 | 기본값 유지 |
| TC-MEMBER-PROFILE-S-01 | 프로필 수정 성공(선택값 누락) | 선택값 null | updateProfile 호출 | 기존 값 유지 |
| TC-MEMBER-PROFILE-S-02 | 프로필 조회 성공(팔로우 집계 포함) | ACTIVE 회원 | getProfile 호출 | 집계 포함 반환 |
| TC-MEMBER-PROFILE-S-03 | 내 프로필 조회 성공(팔로우 집계 포함) | ACTIVE 회원 | getProfileDetail 호출 | 집계 포함 반환 |

## 회원 가입/프로필 실패(추가)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-MEMBER-REG-F-01 | 회원가입 실패(임시 토큰 불일치) | memberId 미존재 | signup 호출 | AUTH-E-000 반환 |
| TC-MEMBER-REG-F-02 | 회원가입 실패(이미 가입 완료) | ACTIVE 회원 | signup 호출 | AUTH-E-020 반환 |
| TC-MEMBER-REG-F-03 | 회원가입 실패(탈퇴 유예기간) | WITHDRAWN + 14일 미경과 | signup 호출 | AUTH-E-021 반환 |
| TC-MEMBER-REG-F-04 | 회원가입 실패(닉네임 중복) | 중복 닉네임 | signup 호출 | MEMBER-E-004 반환 |
| TC-MEMBER-REG-F-05 | 회원가입 실패(DB 유니크 충돌) | 중복 닉네임 경쟁 조건 | signup 호출 | MEMBER-E-004 반환 |
| TC-MEMBER-PROFILE-F-01 | 프로필 수정 실패(탈퇴 회원) | WITHDRAWN 회원 | updateProfile 호출 | MEMBER-E-052 반환 |
| TC-MEMBER-PROFILE-F-02 | 프로필 수정 실패(닉네임 중복) | nickname 변경 + 중복 | updateProfile 호출 | MEMBER-E-004 반환 |
| TC-MEMBER-PROFILE-F-03 | 프로필 수정 실패(성별/키/몸무게 파싱 실패) | invalid gender/height/weight | updateProfile 호출 | MEMBER-E-020/021/023 반환 |
| TC-MEMBER-WITHDRAW-F-01 | 회원 탈퇴 실패(이미 탈퇴) | WITHDRAWN 회원 | withdraw 호출 | MEMBER-E-052 반환 |

## MemberFinder(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-MEMBER-FINDER-S-01 | 활성 회원 조회 성공 | ACTIVE 회원 | findActiveByIdOrThrow 호출 | member 반환 |
| TC-MEMBER-FINDER-F-01 | 활성 회원 조회 실패(미존재/비활성) | memberId 미존재/비활성 | findActiveByIdOrThrow 호출 | MEMBER-E-050 반환 |

## 팔로우(Follow) 서비스(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-MEMBER-FOLLOW-S-01 | 팔로우 성공(응답 집계 포함) | 유효 팔로우 | follow 호출 | 응답 집계 포함 |
| TC-MEMBER-FOLLOW-S-02 | 언팔로우 성공(응답 집계 포함) | 유효 언팔로우 | unfollow 호출 | 응답 집계 포함 |
| TC-MEMBER-FOLLOW-S-03 | 팔로워 목록 조회 성공(커서 포함) | 팔로워 존재 | listFollowers 호출 | 커서 포함 반환 |
| TC-MEMBER-FOLLOW-S-04 | 팔로잉 목록 조회 성공(커서 포함) | 팔로잉 존재 | listFollowings 호출 | 커서 포함 반환 |
| TC-MEMBER-FOLLOW-F-01 | 팔로우 실패(자기 자신) | followerId=followedId | follow 호출 | MEMBER-E-060 반환 |
| TC-MEMBER-FOLLOW-F-02 | 팔로우 실패(중복 팔로우) | 이미 팔로우 | follow 호출 | MEMBER-E-061 반환 |
| TC-MEMBER-FOLLOW-F-03 | 언팔로우 실패(자기 자신) | followerId=followedId | unfollow 호출 | MEMBER-E-060 반환 |
| TC-MEMBER-FOLLOW-F-04 | 언팔로우 실패(중복 언팔로우/관계 없음) | 팔로우 관계 없음 | unfollow 호출 | MEMBER-E-062 반환 |
