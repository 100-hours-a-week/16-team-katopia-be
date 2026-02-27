# 게시글(Post)

| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-S-01 | 게시글 작성 성공 | 인증된 사용자, 유효 본문/이미지/태그 | 게시글 작성 | 201 응답 |
| TC-POST-S-02 | 게시글 목록 조회(커서) | 인증된 사용자, 게시글 존재 | size/after로 조회 | 최신순 목록 + nextCursor |
| TC-POST-S-03 | 게시글 상세 조회 성공 | 게시글 존재 | 상세 조회 | 상세 응답 반환 |
| TC-POST-S-04 | 게시글 수정 성공 | 작성자 본인 | 유효 수정 요청 | 수정 응답 반환 |
| TC-POST-S-05 | 게시글 삭제 성공 | 작성자 본인 | 삭제 요청 | 204 응답 |
| TC-POST-S-06 | 게시글 좋아요 성공 | 인증된 사용자 | 좋아요 요청 | 201 응답 |
| TC-POST-S-07 | 게시글 좋아요 해제 성공 | 좋아요 상태 | 해제 요청 | 204 응답 |
| TC-POST-F-01 | 게시글 작성 실패(본문 없음) | 인증된 사용자 | 빈 본문 | 400 오류 반환 |
| TC-POST-F-02 | 게시글 작성 실패(본문 200자 초과) | 인증된 사용자 | 긴 본문 | 400 오류 반환 |
| TC-POST-F-03 | 게시글 작성 실패(이미지 수량 오류) | 인증된 사용자 | 이미지 0장/4장 | 400 오류 반환 |
| TC-POST-F-04 | 게시글 작성 실패(태그 길이/개수 오류) | 인증된 사용자 | 태그 길이 또는 개수 위반 | 400 오류 반환 |
| TC-POST-F-05 | 게시글 수정 실패(타인) | 작성자 아님 | 수정 요청 | 403 오류 반환 |
| TC-POST-F-06 | 게시글 삭제 실패(타인) | 작성자 아님 | 삭제 요청 | 403 오류 반환 |
| TC-POST-F-07 | 게시글 좋아요 실패(중복) | 이미 좋아요 상태 | 좋아요 요청 | 409 오류 반환 |
| TC-POST-F-08 | 게시글 좋아요 해제 실패(기록 없음) | 미좋아요 상태 | 해제 요청 | 404 오류 반환 |

## 게시글 커맨드(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-CMD-S-01 | 게시글 생성: 본문/이미지/태그 정규화 | 유효 본문/이미지/태그 | create 호출 | 정규화된 값으로 저장 |
| TC-POST-CMD-S-02 | 게시글 수정: 본문 업데이트 및 태그 동기화 | 기존 게시글/태그 | update 호출 | 본문/태그가 동기화됨 |
| TC-POST-CMD-S-03 | 게시글 삭제: 댓글/좋아요/태그 정리 | 기존 게시글 | delete 호출 | 연관 데이터 삭제 후 본문 삭제 |
| TC-POST-CMD-S-04 | 게시글 생성: 태그 누락 허용 | tags=null | create 호출 | 빈 태그 처리 |
| TC-POST-CMD-F-01 | 게시글 수정 실패(작성자 아님) | 타인 게시글 | update 호출 | AUTH-E-014 반환 |
| TC-POST-CMD-F-02 | 게시글 삭제 실패(작성자 아님) | 타인 게시글 | delete 호출 | AUTH-E-014 반환 |

## 게시글 조회(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-QUERY-S-01 | 게시글 목록: 다음 커서 생성 | size와 동일한 게시글 | list 호출 | nextCursor 생성 |
| TC-POST-QUERY-S-02 | 회원별 게시글 목록: 멤버 검증 | 멤버 존재 | listByMember 호출 | 멤버 검증 후 목록 반환 |
| TC-POST-QUERY-S-03 | 게시글 상세: 태그/좋아요 포함 | 게시글/태그/좋아요 존재 | getDetail 호출 | tags/isLiked 포함 |

## 게시글 좋아요(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-LIKE-S-01 | 좋아요 성공 | 좋아요 없음 | like 호출 | 저장 + 카운트 갱신 |
| TC-POST-LIKE-S-02 | 좋아요 알림 생성 | 타인 게시글 | like 호출 | 알림 생성 요청 |
| TC-POST-LIKE-F-01 | 좋아요 실패(중복) | 이미 좋아요 | like 호출 | POST-LIKE-E-001 반환 |
| TC-POST-LIKE-F-02 | 좋아요 해제 실패(기록 없음) | 좋아요 미존재 | unlike 호출 | POST-LIKE-E-002 반환 |

## 게시글 응답 DTO(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-LIKE-RESP-S-01 | 좋아요 응답 변환 | 게시글/likeCount 존재 | PostLikeResponse.of | postId/likeCount 반환 |

## 게시글 작성자 표시(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-AUTHOR-S-01 | 탈퇴 회원 프로필 숨김 | WITHDRAWN 회원 | PostAuthorResponse.of | id/프로필/신체 정보 null |
| TC-POST-AUTHOR-S-02 | 활성 회원 프로필 반환 | ACTIVE 회원 | PostAuthorResponse.of | 원본 프로필 반환 |

## 게시글 본문 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-CONTENT-S-01 | 본문 유효성 성공 | content=유효문자열 | 검증 수행 | 오류 없음 |
| TC-POST-CONTENT-F-01 | 본문 누락(null) | content=null | 검증 수행 | POST-E-000 반환 |
| TC-POST-CONTENT-F-02 | 본문 누락(빈 문자열) | content="" | 검증 수행 | POST-E-000 반환 |
| TC-POST-CONTENT-F-03 | 본문 공백만 | content="   " | 검증 수행 | POST-E-000 반환 |
| TC-POST-CONTENT-F-04 | 본문 길이 초과 | content=201자 | 검증 수행 | POST-E-001 반환 |

## 게시글 태그 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-POST-TAG-S-01 | 태그 null | tags=null | 검증 수행 | 오류 없음 |
| TC-POST-TAG-S-02 | 태그 빈 리스트 | tags=[] | 검증 수행 | 오류 없음 |
| TC-POST-TAG-S-03 | 태그 유효성 성공 | tags=["DAILY","MINIMAL"] | 검증 수행 | 오류 없음 |
| TC-POST-TAG-F-01 | 태그 개수 초과 | tags=11개 | 검증 수행 | POST-E-021 반환 |
| TC-POST-TAG-F-02 | 태그 공백 실패 | tags=[" "] | 검증 수행 | POST-E-020 반환 |
| TC-POST-TAG-F-03 | 태그 길이 위반 | tags=["a"*21] | 검증 수행 | POST-E-020 반환 |

## 이미지 오브젝트 키 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-IMAGE-S-01 | 이미지 유효성 성공 | imageObjectKeys=1~3개 | 검증 수행 | 오류 없음 |
| TC-IMAGE-F-01 | 이미지 리스트 null | imageObjectKeys=null | 검증 수행 | POST-E-010 반환 |
| TC-IMAGE-F-02 | 이미지 리스트 빈 값 | imageObjectKeys=[] | 검증 수행 | POST-E-010 반환 |
| TC-IMAGE-F-03 | 이미지 개수 초과 | imageObjectKeys=4개 | 검증 수행 | POST-E-010 반환 |
| TC-IMAGE-F-04 | 이미지 오브젝트 키 공백 | imageObjectKeys=[" "] | 검증 수행 | POST-E-010 반환 |
