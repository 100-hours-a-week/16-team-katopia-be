# Post Images

## 1) 테이블 설명
게시글 이미지(ElementCollection) 저장 테이블.

## 2) 관계
- posts(1) : post_images(N)

## 3) 설계 근거
- 게시글의 구성 요소이므로 별도 엔티티가 아닌 값 컬렉션으로 관리한다.
- 정렬/대표 이미지 조회를 위해 sort_order를 유지한다.

## 4) 컬럼 정의
No | 컬럼 | 타입 | 제약 | 설명
---|---|---|---|---
1 | post_id | BIGINT | fk, nn | 게시글 ID
2 | sort_order | INT | nn | 이미지 순서
3 | image_object_key | VARCHAR(1024) | nn | 이미지 오브젝트 키

## 5) 인덱스/제약
- `uk_post_images_post_order (post_id, sort_order)`
- `idx_post_images_order (post_id, sort_order)`

## 6) 운영 정책
- 이미지 순서는 1부터 시작, 최대 개수는 서비스 정책에서 제한
- 대표 이미지는 sort_order=1 기준으로 사용
