# 비동기 이벤트 아키텍처 (Asynchronous Event Architecture)

## 1. 목적 및 범위
> 멀티 인스턴스 환경에서 집계/알림/채팅을 비동기로 수행해 응답 지연과 장애 전파를 줄인다.

### 문제 정의
- 동기 처리만으로는 이벤트 처리 지연과 리소스 경쟁이 발생한다.
- 실시간 전달(SSE/WS)과 저장은 분리되어야 유실을 방지할 수 있다.

### Non-goals
- 글로벌 멀티리전 메시징: 다중 리전의 복잡도/비용이 현재 목표에 비해 과도하다.
- 스트림 분석/리플레이 기반 BI: 데이터 분석 파이프라인 요구가 확정되지 않았고, 이벤트 보관/리플레이 비용이 크다.
- 알림 등급(중요/일반) 분리 정책: 알림 비활성화는 존재하지만, 알림 별 등급은 요구사항에서 고려되지 않음.

## 2. 비동기 이벤트 흐름 개요

### 집계(댓글)

| Event | Trigger | Consumer | Idempotency | Retry |
| --- | --- | --- | --- | --- |
| CommentCountDeltaPublished | 댓글 생성 | CommentCountBatchWorker | Redis INCRBY 원자 연산 | 없음(복구 정책으로 보완) |

- 처리: 핫트래픽인 댓글 생성/삭제에 대한 집계를 Redis 원자적 연산으로 delta를 누적한다.
- 키: `count:comment:delta:{postId}` (postId 해시태그로 동일 키 슬롯 유지)
- 동기화: `3초` 주기로 `delta != 0`인 게시글을 찾아 DB에 반영한다.
- 복구: Redis 장애 시 last batch 이후 생성/삭제된 댓글만 재계산해 정합성을 맞춘다.

### 실시간 알림
- 전송: best-effort 방식의 SSE 전송을 지원한다.
- 보관: 실시간 알림 여부와 관계없이 알림은 DB 저장한다.
- 전파 정책:
  - 투표 종료: 해당 투표에 참여한 모든 사용자에게 알림(1 -> N fan-out)
  - 게시글 작성: 작성자를 팔로우한 모든 사용자에게 알림(1 -> N fan-out)
  - 좋아요/팔로우/댓글: 트리거는 다수일 수 있으나 수신자는 1명(N -> 1)
 - fan-out 전송이 병목이 되면 @Async 기반 전송 분리를 우선 고려한다.
   - 이후 처리량/장애 격리 요구가 커지면 MQ 기반 전송 분리로 확장한다.

### 채팅
- 지원 범위: 그룹 채팅(N:M)
- 전송: WebSocket/STOMP + Redis Pub/Sub 기반 best-effort 실시간 전송을 지원한다.
- 순서: 채팅 방 단위로 전송 순서를 보장한다.
- 보관: 현재는 MongoDB에 동기 저장(write-through)하고, 처리량/장애 격리 요구가 커지면 MQ로 저장/후처리를 분리한다.

## Redis


### 멱등성 기준
- eventId(UUID)를 멱등 키로 사용한다.
- 저장 계층에는 고유 제약 또는 UPSERT로 중복 처리를 방지한다.

### 메시징 인프라
- RabbitMQ: 저장/집계/비동기 처리용
- Redis Pub/Sub: 실시간 전파용

### 명명 규칙(교환기/큐)
- `domain_events`는 "도메인 이벤트를 단일 진입점으로 모으는 교환기"라는 의미로 사용한다.
  - 발행자는 이벤트 타입만 알고, 라우팅은 교환기에서 담당해 결합도를 낮춘다.
- `notification_management_queue`는 "비즈니스 알림 저장/전송을 담당하는 큐" 의미로 사용한다.
  - 알림 도메인의 처리 책임을 드러내고, 향후 알림 전송/저장 분리 시 큐 역할을 명확히 한다.

### 이벤트 스키마(메시지 포맷)
- 공통 필드:
  - eventId (UUID)
  - eventType
  - occurredAt (ISO8601)
  - actorId
  - targetId
  - refId (post/comment/vote 등)
  - payload (타입별 상세)
  - payload는 eventType별로 개별 스키마를 정의한다(도메인별 필드/스냅샷 포함).
- 알림 이벤트는 2단계 페이로드를 사용한다.
  - 1차 큐(알림 타깃 큐): NotificationMetaPayload (타깃 계산에 필요한 최소 정보)
  - 2차 큐(알림 배치 큐): NotificationPayload (targetIds + messageArgs 포함)

### 큐/토픽 구성
- exchange: `domain_events`
- queue:
  - `notification_target_queue`
  - `notification_batch_queue`
  - `aggregate_queue`
  - `chat_persist_queue`
  - `chat_fanout_queue`
- routing key:
- `notification.meta` (알림 타깃 이벤트 소비)
  - `notification.batch` (알림 배치 이벤트 소비)
- DLQ:
  - `notification_target_dlq`
  - `notification_batch_dlq`
  - `aggregate_dlq`
  - `chat_persist_dlq`
  - `chat_fanout_dlq`

### MQ 장애 대응(발행/복구)
- 브로커가 다운되면 발행 자체가 실패하므로, 이때는 재시도 또는 Outbox 같은 별도 기록 방식이 필요하다.
- 브로커 정상 상태에서 발행된 메시지는 durable 설정을 통해 큐에 남아 복구 후 재처리된다.
- 결론: MQ는 "발행 성공 이후"에는 복구 가능하지만, 발행 자체 실패에 대한 보호는 별도 정책이 필요하다.


## 4. 실시간 알림(SSE)
```bash
GET /api/notifications/stream
```
- 단일 SSE 스트림에서 `notification`(비즈니스 알림)과 `chat`(채팅 알림)을 이벤트 이름으로 구분한다.
- `알림 끔 설정`은 `notification` 전송만 차단하며, 채팅 알림(`chat`)은 **채팅방별 on/off 설정**을 따른다.


### 실시간 알림 이벤트 흐름
```mermaid
sequenceDiagram
  autonumber
  participant Client as Client
  participant API as API Server
  participant MQ as RabbitMQ
  participant MetaWorker as Notification Target Worker
  participant BatchWorker as Notification Batch Worker
  participant Realtime as Notification Realtime Worker
  participant DB as MySQL
  participant Redis as Redis Pub/Sub

  Client->>API: 알림 이벤트 트리거
  API->>MQ: 알림 타깃 이벤트 발행
  MQ-->>MetaWorker: 타깃 이벤트 전달
  MetaWorker->>MQ: 배치 이벤트 발행(targetIds 포함)
  MQ-->>BatchWorker: 배치 이벤트 전달
  BatchWorker->>DB: 알림 저장
  BatchWorker-->>Redis: SSE 전송 신호 발행(Pub/Sub)
  Redis-->>Realtime: SSE 전송 이벤트 수신
  Realtime-->>API: SSE 연결 인스턴스로 알림 전파
  API-->>Client: SSE 실시간 알림 전송(best-effort)
```

### 실시간 알림 분기/배치 흐름(큐 경로)
```mermaid
flowchart TD
  subgraph RMQ["RabbitMQ"]
    EX -->|POST_CREATED/VOTE_CLOSED| Q1[notification_target_queue]
    EX -->|FOLLOW/POST_LIKE/POST_COMMENT| Q2[notification_batch_queue]
  end

  Q1 --> TargetConsumer[Notification Target Consumer]
  TargetConsumer -->|batch split targetIds <=200| EX

  Q2 --> BatchConsumer[Notification Batch Consumer]
  BatchConsumer --> DB[(MySQL)]
  BatchConsumer --> PubSub((Redis Pub/Sub))
  PubSub --> SSE[SSE Realtime Publisher]
```

### 실시간 알림 저장/전송 흐름(구현 상세)
```mermaid
graph LR
		subgraph Trigger [Notification Trigger Event]
			SingleTargetEvent@{ shape: event, label: "Single Target"}
			MultiTargetEvent@{ shape: event, label: "Multi Target"}
		end

		SingleTargetEvent move@=="Pub"==> BatchQueue
    move@{ animation: fast }

		MultiTargetEvent move1@=="Pub"==> TargetQueue
		move1@{ animation: fast }

    subgraph Message_Queue [Message Queue]
        TargetQueue@{ shape: das, label: "Target Queue" }
        BatchQueue@{ shape: das, label: "Batch Queue" }
    end

    subgraph logicalGraph [ ]
      Message_Queue
	    TargetConsumer@{ shape: procs, label: "Target Consumer"}
	    BatchConsumer@{ shape: procs, label: "Batch Consumer"}
    end

    TargetQueue move2@==> TargetConsumer
    TargetConsumer move3@=="Pub"==> BatchQueue
		move2@{ animation: fast }
		move3@{ animation: fast }


    Database[(Database)]
    Redis@{ shape: hex, label: "Redis Pub/Sub" }

    BatchConsumer --"1️⃣ save"--> Database
    BatchQueue -."2️⃣ Ack after save".- BatchConsumer
    BatchQueue ----> BatchConsumer
    BatchConsumer -- "3️⃣ async send" --> Redis

    subgraph logicalGraph2 [ ]
	    Redis --> ServerA --> User
	    Redis --> ServerB --> User
	    User((SSE Clients))
    end


style logicalGraph fill:none,stroke:none
style logicalGraph2 fill:none,stroke:none
style Message_Queue fill:#f5f5f5,stroke:#333
style TargetQueue fill:#fff,stroke:#333
style BatchQueue fill:#fff,stroke:#333
style Database fill:#e1f5fe,stroke:#01579b
style Trigger fill:#fff3e0,stroke:#ff6f00


```

### 보장 수준
- 실시간 전송: best-effort
- 알림 저장: at-least-once

### 알림 MQ 2단계 처리 정책
- 1차 큐(알림 타깃 큐): 알림 대상 계산 전용(팬아웃 대상)
  - 타깃 소비자는 대상 계산 후, 배치 이벤트를 2차 큐에 모두 발행한 뒤에만 ack 한다.
  - 목적: 대상 계산/발행 중 유실을 방지하고, 2차 큐 적재 완료를 1차 큐 ack의 기준으로 삼는다.
- 2차 큐(알림 배치 큐): 알림 저장/전송 전용
  - 배치 소비자는 알림 저장 성공을 ack 기준으로 삼는다.
  - 실시간 전송(SSE)은 저장 이후 best-effort이며 ack 기준과 분리한다.
- 배치 분할 기준: targetIds 최대 100개 단위로 분할 발행한다.
- 단일 대상(좋아요/댓글/팔로우)은 2차 배치 큐로 직접 발행하고, 게시글 작성/투표 종료만 타깃 큐를 경유한다.
- targetIds 정책
  - 2차 배치 큐 이벤트에는 항상 targetIds가 채워져야 한다.
- 타깃 큐 이벤트는 targetIds가 비어있으며, 타깃 소비자가 대상 계산 후 채운다.


<br>

### SSE 연결 제한/종료 전파 흐름

```mermaid
sequenceDiagram
  autonumber
  participant Client as Client
  participant API1 as API Server (Instance A)
  participant API2 as API Server (Instance B)
  participant Redis as Redis (ZSET + Pub/Sub)

  Client->>API1: SSE 연결 요청
  API1->>Redis: ZADD sse:notification:{memberId} (connectionId(UUID), connectedAt)
  API1->>Redis: ZCARD sse:notification:{memberId}
  alt 연결 초과(>3)
    API1->>Redis: ZPOPMIN sse:notification:{memberId}
    API1->>Redis: PUBLISH sse:notification:disconnect (memberId|connectionId(UUID))
  end
  Redis-->>API1: 종료 이벤트 구독 수신
  Redis-->>API2: 종료 이벤트 구독 수신
  API1->>API1: 로컬 connectionId(UUID) 매칭 시 SSE 종료
  API2->>API2: 로컬 connectionId(UUID) 매칭 시 SSE 종료
  API1->>Redis: ZREM sse:notification:{memberId} (connectionId(UUID))
```

#### 설명
- SSE 연결 시 Redis ZSET에 connectionId를 등록한다. Redis에 연결된 사용자의 SSE 연결 개수를 확인(ZCARD)한다.
- 3개를 초과하면 가장 오래된 connectionId를 제거하기 위해 종료 이벤트를 Pub/Sub으로 전파한다.
- 모든 인스턴스는 종료 이벤트를 구독하며, 자신이 보유한 connectionId면 SSE를 종료한다.
- 종료된 connectionId는 Redis에서도 제거해 상태를 정리한다.
  - 초과 연결 제거 시에는 ZPOPMIN으로 이미 제거된 connectionId에 대해 ZREM이 중복 호출될 수 있다. 이 경우 ZREM은 no-op이므로 안전하다.
  - ZREM은 정상 종료/타임아웃/에러 등 다양한 경로에서 Redis 상태를 일관되게 정리하기 위한 최종 정리 단계로 호출한다.
- Redis 키는 `sse:notification:{memberId}`으로 하여 동일 사용자 SSE 연결을 동일 슬롯으로 묶는다.
  - `sse`는 실시간 연결 카테고리, `notification`은 도메인 구분을 위한 prefix다. 채팅 실시간 알림은 `chat`으로 구분된다.
  - `{memberId}` 해시태그는 Redis Cluster 환경에서 동일 사용자 키를 같은 슬롯으로 묶기 위함이다.
- 값(ZSET): `connectionId(UUID)` -> `connectedAt(epoch ms)`
  - 연결 시간 순 정렬과 `ZPOPMIN` 기반의 오래된 연결 제거를 위해 ZSET을 사용한다.
  - `connectionId`는 동일 ms 충돌을 피하고, 특정 SSE emitter를 정확히 종료하기 위한 식별자다.

#### 인증
- `Authorization: Bearer {AT}` 헤더 기반

#### 응답 헤더
SSE/HTTP 표준에 따른 권장 설정

- `Content-Type: text/event-stream`
- `Cache-Control: no-cache`
- `Connection: keep-alive`

#### 이벤트 포맷
```
id: {eventId}
event: notification
data: {"id":1,"type":"POST_LIKE","message":"...","refId":100}
```
```
id: {eventId}
event: chat
data: {"roomId":10,"senderId":2,"message":"..."}
```

#### 재연결/재전송 정책
- 클라이언트는 자동 재연결(EventSource 기본 동작).
- 서버는 재접속 시 최근 10건의 미수신 알림을 재전송한다: 재접속 직후 UX를 보장하면서도 과도한 재전송 부하를 방지하기 위함.
- 미수신 기준은 `read_at is null`이며, 최근 10건을 `created_at desc`로 조회한다.
- 서버 SSE 타임아웃은 5분으로 운용한다(연결 유지를 보장하면서도 유휴 연결을 정리하기 위함).
- 재연결 시점(클라이언트가 새 SSE 연결을 수립하는 순간)에 새 connectionId가 Redis에 등록된다.
- `ping` 이벤트 전송 주기는 50초로 운용한다(ALB idle timeout 60초 기준). 하트비트 스케줄은 활성 상태다.

#### 동시 접속 제한
- 사용자당 SSE 연결은 최대 3개까지 허용한다.
- 3개를 초과하는 신규 연결이 들어오면 가장 오래된 연결을 종료한다.
- 연결 상태는 Redis에 저장해 인스턴스 간 동시 접속 제한을 강제한다.
- 연결 단위로 관리하며, 활성 기기가 재연결될 때 비활성 기기 연결이 밀리는 것을 허용한다(활성 기기 우선 정책).
- 가장 오래된 연결 종료는 Redis Pub/Sub으로 종료 이벤트를 전파한다.
- 종료 이벤트 채널명은 `sse:notification:disconnect`를 사용한다.
- 종료 이벤트 메시지는 최소 `{memberId, connectionId}` 형식을 사용한다.

#### 하트비트(그룹 분산 전송)
- 하트비트는 로컬 인스턴스가 보유한 SSE 연결에만 전송한다.
- 연결 수가 많을 때 동시 전송 부하를 줄이기 위해 10개 그룹으로 분산한다.
- 각 하트비트 주기마다 전체 연결 중 1개 그룹만 ping을 전송한다.

```mermaid
sequenceDiagram
  autonumber
  participant Scheduler as Heartbeat Scheduler
  participant API as API Server
  participant Conn as Local SSE Connections

  Scheduler->>API: sendHeartbeat()
  API->>API: 그룹 인덱스 선택(0~9)
  API->>Conn: 해당 그룹 connectionId에만 ping 전송
```

#### 운영/스케일링 고려
- SSE 연결은 장시간 유지되므로, 서버 스레드 점유가 최소화되도록 비동기(서블릿 async) 처리로 운용한다.
- 수평 확장 시 Sticky Session 또는 Redis Pub/Sub으로 인스턴스 간 팬아웃을 고려한다.
  - SSE 연결은 인스턴스 로컬에 매달리므로 라우팅 정책이 중요하다.
- 동시 접속 제한을 전역으로 강제하기 위해 Redis에 연결 상태를 저장한다(Sticky Session의 부하 분산 한계 보완).
- Redis 장애 시에는 실시간 알림(SSE 전송)을 제공하지 않고, 알림함 조회로 대체한다(서비스 성격상 실시간 전송은 필수가 아님).
- Redis 운영은 단일 논리 클러스터(단일 Redis/Redis Cluster) 전제를 따른다.

#### 알림 저장/보관 정책
- 알림은 DB에 저장한다.
- 생성일 기준 30일이 지나면 읽음 여부와 무관하게 삭제한다.

#### SSE vs WebSocket 선택 근거
- 알림은 단방향이므로 SSE가 적합.
- SSE는 HTTP 업그레이드 없이 `text/event-stream` 기반으로 유지되며, WebSocket 업그레이드/전용 타임아웃/세션 고정 설정 같은 프록시/LB 설정 부담이 상대적으로 낮다.
- 단방향 스트림이므로 서버는 클라이언트 전송 채널(예: ping/pong, 백프레셔, 송신 큐)과 같은 양방향 상태를 별도로 관리하지 않아도 되고, 메시지 전송/재연결은 클라이언트(EventSource)가 주도한다.
- 다만 SSE도 장시간 연결을 유지하므로 만료/정리(유령 연결 정리, 다중 인스턴스 전파)는 필요하다.
- 채팅은 양방향이므로 WebSocket을 사용한다.
- 채널 역할을 분리해 알림(SSE)과 채팅(WebSocket)의 운영/스케일링 요구사항을 분리한다.

### 재시도/DLQ 정책
- 재시도 대상: 알림 저장 실패에 한정한다.
- 실시간 전송(SSE) 실패는 재시도하지 않는다.

### 복구(브로커 장애)
- 복구 전제: durable queue + persistent message + manual ack가 설정되어 있어야 한다.
- 브로커가 재기동되면 큐에 남아 있던 메시지는 복구되어 재처리된다.
- ack 이전에 소비자가 종료된 메시지는 재큐잉되어 다시 소비된다.
 - TODO: 실시간 알림 전송을 MQ로 전환한 이후, durable/persistent/manual ack 설정을 코드로 보장한다.

## 5. 채팅 비동기 처리(정리)

### 기본 정책(동기 저장)
- 채팅은 기본적으로 **동기 저장(write-through)** 후 전파한다.
- 순서 보장: 채팅방 단위로만 보장한다.
- 실시간 전송: best-effort
- 멀티 인스턴스 전파: Redis Pub/Sub

### 현재 구현 책임 분리

```text
+----------------------+----------------------------------------------+
| 구성요소             | 현재 책임                                     |
+----------------------+----------------------------------------------+
| WebSocket/STOMP      | 클라이언트 연결, 인증, room 단위 송수신       |
| MongoDB              | 채팅방/참여/메시지 영속 저장                  |
| Redis Pub/Sub        | 인스턴스 간 실시간 메시지/읽음 상태 fan-out   |
| RabbitMQ             | 채팅에는 아직 미적용, 후속 비동기 분리 후보    |
+----------------------+----------------------------------------------+
```

### 현재 구현 플로우
```mermaid
sequenceDiagram
  autonumber
  participant Client as Client
  participant WS as Chat WS API
  participant DB as MongoDB
  participant Redis as Redis Pub/Sub
  participant Other as Other WS Instances

  Client->>WS: SEND(MESSAGE / READ_STATE)
  WS->>DB: save / update
  WS->>Redis: publish(chat:realtime)
  Redis-->>WS: event fan-out
  Redis-->>Other: event fan-out
  WS-->>Client: /topic/chat/rooms/{roomId}/*
  Other-->>Client: /topic/chat/rooms/{roomId}/*
```

### 최종 책임 분리 방향
- Redis Pub/Sub: "지금 붙어 있는 세션"에 대한 실시간 fan-out
- MongoDB: 채팅방/참여자/메시지의 최종 저장소
- RabbitMQ: 저장/후처리/재시도/DLQ가 필요한 비동기 경로

### 최종 책임 분리 다이어그램
```mermaid
flowchart LR
  Client[Client]
  WS[WebSocket API]
  Redis[(Redis Pub/Sub)]
  Mongo[(MongoDB)]
  MQ[(RabbitMQ)]
  Persist[Chat Persist Worker]
  Fanout[Chat Realtime Fan-out]

  Client -->|SEND MESSAGE/READ_STATE| WS
  WS -->|현재: 동기 저장| Mongo
  WS -->|실시간 전파 이벤트| Redis
  Redis --> Fanout
  Fanout -->|local /topic/chat/...| Client

  WS -. 확장 시 .-> MQ
  MQ --> Persist
  Persist --> Mongo
  Persist -->|저장 완료 후 실시간 전파| Redis
```

### 비동기 전환 후보
- WS 수신 → MQ 적재 → 워커 저장 → Redis Pub/Sub 실시간 전파
- 목적: 저장 장애 격리 및 수평 확장

### 비동기 전환 플로우(mermaid)
```mermaid
sequenceDiagram
  autonumber
  participant Client as Client
  participant WS as WS Gateway
  participant MQ as RabbitMQ
  participant W as ChatPersistWorker
  participant DB as MongoDB
  participant Redis as Redis Pub/Sub

  Client->>WS: SEND(MESSAGE)
  WS->>MQ: enqueue(message)
  MQ->>W: consume(message)
  W->>DB: save(message)
  W->>Redis: publish(chat:realtime)
  Redis-->>WS: fan-out event
  WS-->>Client: BROADCAST(MESSAGE)
```

### 읽음 상태 전파(요약)
- 참여자별 lastReadMessageId를 브로드캐스트한다.
- 단일 토픽 운영 시 eventType(READ_STATE)로 구분한다.

### 재시도/DLQ 정책
- 저장 실패는 재시도 후 DLQ로 격리한다.
- 실시간 전송 실패는 best-effort로 처리한다.

## 6. 댓글 집계 비동기 처리

### 집계(댓글) 시퀀스
```mermaid
sequenceDiagram
  autonumber
  participant Client as Client
  participant API as API Server
  participant Redis as Redis
  participant Batch as CommentCountBatchWorker(10s)
  participant DB as MySQL

  Client->>API: 댓글 생성/삭제
  API->>DB: 댓글 저장/소프트 삭제
  API->>Redis: delta 증가/감소(INCRBY)
  Batch->>Redis: delta != 0 조회
  Batch->>DB: comment_count += delta
  Batch->>DB: batch 이력 기록(count_batches)
```

### 보장 수준
- Redis delta 적립: at-least-once
- DB 반영: at-least-once

### 결정 근거
- 동기 집계를 단순 적용했을 때, 100명 사용자가 1분 동안 댓글을 작성/삭제하는 상황을 가정하고 슬립 3초로 부하를 준 실험에서 데드락이 발생했다.
- 데드락 완화를 위해 집계 경로를 비동기로 분리했고, Redis는 분리된 집계의 delta 누적 매체로 사용한다(정합성은 배치/복구 정책으로 보완).

### 복구 정책
- 기준 시각: `count_batches.processed_to`를 마지막 정상 처리 시각으로 사용한다.
- 복구 범위: 마지막 처리 시각 이후의 댓글 생성/삭제만 재계산한다.
- 삭제 기준: 소프트 삭제(`deleted_at`) 시각을 기준으로 포함/제외한다.
- 비용 제한: 전체 `count(*)`는 피하고, 변경 구간만 재계산한다.

### 재시도/DLQ 정책
- 재시도 대상: 집계 반영 실패에 한정한다.
- Redis 적립 실패는 재시도 대상이 아니며, 동기 반영도 하지 않고 복구 정책으로 보완한다.

## 7. 재처리/복구 정책 요약

### 실시간 알림 이벤트 재처리 정책
> 실시간 알림은 best-effort이며, 전송 실패 시 재처리하지 않는다.
> 실패 복구는 알림함(DB) 조회로 보완한다.

| Trigger   | Consumer | Retry | Rollback | 
|-----------|---|-------|----------|
| 팔로우 생성    | NotificationRealtimeWorker | -     | -        |
| 게시글 좋아요   | NotificationRealtimeWorker | -     | -        |
| 게시글 댓글 작성 | NotificationRealtimeWorker | -     | -        |
| 투표 종료     | NotificationRealtimeWorker | -     | -        |

### 알림 저장 실패
- DB 저장 실패는 도메인 정책에 따라 재시도 후 DLQ로 격리한다.
- DLQ는 수동/배치 재처리로 운영한다.

### 댓글 집계 복구
- Redis delta 유실 시 last batch 이후 생성/삭제된 댓글만 재계산한다.

## 8. 고민과정 및 향후 개선
- fan-out 최적화: memberId 단위 전파 vs payload 전파 기준 정리 필요.
- Redis 장애 대응: SSE 비활성화 후 알림함 조회로 보완.
- 멀티 인스턴스 라우팅: Sticky Session vs Pub/Sub 비용 비교.
- 실시간 전송 지연 허용 범위: 도메인별 SLA 확정 필요.

## 9. 관련 문서
- `docs/domain-tech-spec/NOTIFICATION_TECH_SPEC.md`
- `docs/chat/CHAT_TECH_SPEC.md`
- `docs/DELETE_AND_VISIBILITY_POLICY.md`
- `docs/table/COUNT_BATCHES.md`

### 용어 정의
- Publisher: 이벤트를 생성해 브로커/채널로 발행하는 주체(API/서비스/워커)
- Consumer: 브로커/채널에서 이벤트를 수신해 처리하는 주체(워커/인스턴스)
- SSE: Server-Sent Events, 서버 -> 클라이언트 단방향 스트림
- DLQ: Dead Letter Queue, 재시도 실패 메시지 보관 큐
- fan-out: 하나의 이벤트를 여러 소비자/대상으로 복제 전달
