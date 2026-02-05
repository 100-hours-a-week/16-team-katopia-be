# Member Styles

## 1) 테이블 설명
회원의 선호 스타일을 저장하는 테이블(ElementCollection).  
스타일은 고정된 Enum 값을 사용하며, 서비스 정책상 최대 2개까지 허용한다.

## 2) 관계
- members(1) : member_styles(N)

## 3) 설계 근거
- 사용자 프로필의 일부이므로 별도 엔티티가 아닌 ElementCollection으로 관리한다.
- style 중복은 서비스 검증과 유니크 제약으로 방지한다.

## 4) 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | member_id | BIGINT | fk, nn | 회원 ID
2 | style | VARCHAR(30) | nn | 스타일 값(ENUM 문자열)

## 5) 인덱스/제약
- JPA ElementCollection 기반 생성(명시적 인덱스 없음)

## 6) 운영 정책
- 스타일 목록은 검증기로 유효성/최대 개수 제한을 강제
- 저장 시 대문자 Enum 문자열로 정규화
