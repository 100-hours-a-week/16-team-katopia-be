# 친구 추천 도메인 테크 스펙 (Recommendation)

## 배경 (Background)

- 프로젝트 목표: 홈 화면에서 빠르게 팔로우할 수 있는 후보를 제공한다.
- 문제 정의:
  - 신규 사용자는 팔로우 관계가 없어 추천 품질이 낮다.
  - 추천 기준과 fallback 규칙이 불명확하면 일관성이 깨진다.

## 목표가 아닌 것 (Non-goals)

- ML 기반 추천
- 팔로우 그래프 기반 고도화

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 추천 기준(1순위)
  - 내가 팔로우한 사람의 팔로우 목록 중, 내가 팔로우하지 않은 사용자
- fallback(2순위)
  - 1순위 후보가 없으면 최신 가입자
  - 내가 팔로우한 사람이 없으면 최신 가입자
- 제외 대상
  - 본인
  - ACTIVE가 아닌 계정

### 조회 전략

- 정렬: 최신 가입자 순(createdAt desc, id desc)
- 최대 30명 고정 반환 (페이징 없음)

## API 명세 (API Specifications)

- 추천 목록: `GET /api/home/members`

## 장애/예외 처리

- 인증 실패는 401

## 변경 사항

- 추천 응답은 전용 스키마로 제공(id/nickname/profileImageObjectKey/height/weight/styles)

## 용어 정의 (Glossary)

- 추천 범위: 키/몸무게 기준 허용 편차
