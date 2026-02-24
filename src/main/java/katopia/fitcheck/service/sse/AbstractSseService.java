package katopia.fitcheck.service.sse;

import katopia.fitcheck.global.policy.Policy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractSseService<T> {

    private final Map<String, SseConnection> connections = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> memberConnections = new ConcurrentHashMap<>();

    /**
     * SSE 연결을 생성하고 로컬 레지스트리에 등록한 뒤 초기 데이터를 전송한다.
     *
     * @param memberId        연결 대상 사용자 ID
     * @param initialPayloads 연결 직후 전송할 초기 페이로드 목록
     * @return 컨트롤러 응답 본문으로 사용할 {@link SseEmitter}
     */
    public SseEmitter connect(Long memberId, List<T> initialPayloads) {
        String connectionId = newConnectionId();
        long connectedAt = System.currentTimeMillis();
        onConnected(memberId, connectionId, connectedAt);

        SseEmitter emitter = new SseEmitter(Policy.SSE_TIMEOUT.toMillis());
        registerConnection(memberId, connectionId, emitter);

        // 연결 종료/타임아웃/에러 발생 관리
        emitter.onCompletion(() -> disconnect(connectionId));
        emitter.onTimeout(() -> disconnect(connectionId));
        emitter.onError(ex -> disconnect(connectionId));

        // 초기 연결 및 초기 데이터 순차 전송
        sendPing(connectionId);
        sendInitial(connectionId, initialPayloads);
        return emitter;
    }

    /**
     * 기본 이벤트 이름으로 해당 사용자의 모든 활성 연결에 페이로드를 전송한다.
     *
     * @param memberId 전송 대상 사용자 ID
     * @param payload  전송할 페이로드
     */
    public void send(Long memberId, T payload) {
        Set<String> connectionIds = memberConnections.get(memberId);
        if (connectionIds == null || connectionIds.isEmpty()) {
            return;
        }
        for (String connectionId : connectionIds) {
            sendToConnection(connectionId, payload);
        }
    }

    /**
     * 지정한 이벤트 이름으로 해당 사용자의 모든 활성 연결에 페이로드를 전송한다.
     *
     * @param memberId  전송 대상 사용자 ID
     * @param eventName 이벤트 이름(비어 있으면 기본 이벤트 이름 사용)
     * @param payload   전송할 페이로드
     */
    public void sendEvent(Long memberId, String eventName, Object payload) {
        Set<String> connectionIds = memberConnections.get(memberId);
        if (connectionIds == null || connectionIds.isEmpty()) {
            return;
        }
        for (String connectionId : connectionIds) {
            sendEventToConnection(connectionId, eventName, payload);
        }
    }

    /**
     * 특정 연결을 종료하고 로컬 레지스트리에서 제거한 뒤 훅을 호출한다.
     *
     * @param connectionId 종료할 연결 ID
     */
    public void disconnect(String connectionId) {
        SseConnection connection = connections.remove(connectionId);
        if (connection == null) {
            return;
        }
        Set<String> connectionIds = memberConnections.get(connection.memberId);
        if (connectionIds != null) {
            connectionIds.remove(connectionId);
            if (connectionIds.isEmpty()) {
                memberConnections.remove(connection.memberId);
            }
        }
        onDisconnected(connection.memberId, connection.connectionId);
        connection.emitter.complete();
    }

    /**
     * 연결 생성 직후 호출되는 훅이다. 구현체에서 외부 저장소(예: Redis)에
     * 연결 상태를 기록하는 용도로 사용한다.
     *
     * @param memberId     사용자 ID
     * @param connectionId 연결 ID
     * @param connectedAt  연결 시각(epoch ms)
     */
    protected void onConnected(Long memberId, String connectionId, long connectedAt) {
    }

    /**
     * 연결이 제거되거나 종료될 때 호출되는 훅이다.
     *
     * @param memberId     사용자 ID
     * @param connectionId 연결 ID
     */
    protected void onDisconnected(Long memberId, String connectionId) { }

    /**
     * 이 SSE 스트림의 기본 이벤트 이름을 반환한다.
     *
     * @return 기본 이벤트 이름
     */
    protected abstract String eventName();

    /**
     * 기본 이벤트 이름으로 SSE 이벤트를 생성한다.
     *
     * @param payload 전송할 페이로드
     * @return SSE 이벤트 빌더
     */
    protected SseEmitter.SseEventBuilder toEvent(T payload) {
        return SseEmitter.event()
                .name(eventName())
                .data(payload);
    }

    /**
     * 지정한 이벤트 이름으로 SSE 이벤트를 생성한다.
     * 이벤트 이름이 비어 있으면 기본 이벤트 이름을 사용한다.
     *
     * @param eventName 이벤트 이름
     * @param payload   전송할 페이로드
     * @return SSE 이벤트 빌더
     */
    protected SseEmitter.SseEventBuilder toEvent(String eventName, Object payload) {
        String resolved = (eventName == null || eventName.isBlank()) ? eventName() : eventName;
        return SseEmitter.event()
                .name(resolved)
                .data(payload);
    }

    /**
     * 초기 페이로드가 존재할 경우 특정 연결에 순차 전송한다.
     *
     * @param connectionId  전송 대상 연결 ID
     * @param initialPayloads 초기 전송할 페이로드 목록
     */
    protected void sendInitial(String connectionId, List<T> initialPayloads) {
        if (initialPayloads == null || initialPayloads.isEmpty()) {
            return;
        }
        for (T payload : initialPayloads) {
            sendToConnection(connectionId, payload);
        }
    }

    /**
     * 연결 유지와 장애 감지를 위해 ping 이벤트를 전송한다.
     *
     * @param connectionId 전송 대상 연결 ID
     */
    protected void sendPing(String connectionId) {
        SseConnection connection = connections.get(connectionId);
        if (connection == null) {
            return;
        }
        try {
            connection.emitter.send(SseEmitter.event()
                    .name("ping")
                    .data("connected"));
        } catch (IOException ex) {
            disconnect(connectionId);
            log.debug("SSE ping failed for memberId={}", connection.memberId, ex);
        }
    }

    /**
     * 모든 활성 연결에 ping 이벤트를 전송한다.
     */
    protected void sendHeartbeat() {
        for (String connectionId : connections.keySet()) {
            sendPing(connectionId);
        }
    }

    /**
     * 기본 이벤트 이름으로 단일 연결에 페이로드를 전송한다.
     *
     * @param connectionId 전송 대상 연결 ID
     * @param payload      전송할 페이로드
     */
    protected void sendToConnection(String connectionId, T payload) {
        SseConnection connection = connections.get(connectionId);
        if (connection == null) {
            return;
        }
        try {
            connection.emitter.send(toEvent(payload));
        } catch (IOException ex) {
            disconnect(connectionId);
            log.debug("SSE send failed for memberId={}", connection.memberId, ex);
        }
    }

    /**
     * 지정한 이벤트 이름으로 단일 연결에 페이로드를 전송한다.
     *
     * @param connectionId 전송 대상 연결 ID
     * @param eventName    이벤트 이름
     * @param payload      전송할 페이로드
     */
    protected void sendEventToConnection(String connectionId, String eventName, Object payload) {
        SseConnection connection = connections.get(connectionId);
        if (connection == null) {
            return;
        }
        try {
            connection.emitter.send(toEvent(eventName, payload));
        } catch (IOException ex) {
            disconnect(connectionId);
            log.debug("SSE send failed for memberId={}", connection.memberId, ex);
        }
    }

    /**
     * 조회 및 fan-out을 위해 로컬 레지스트리에 연결을 등록한다.
     *
     * @param memberId     사용자 ID
     * @param connectionId 연결 ID
     * @param emitter      SSE emitter
     */
    private void registerConnection(Long memberId, String connectionId, SseEmitter emitter) {
        connections.put(connectionId, new SseConnection(memberId, connectionId, emitter));
        memberConnections.computeIfAbsent(memberId, id -> ConcurrentHashMap.newKeySet())
                .add(connectionId);
    }

    /**
     * SSE 세션을 추적하기 위한 고유 연결 ID를 생성한다.
     *
     * @return 연결 ID(UUID)
     */
    private String newConnectionId() {
        return UUID.randomUUID().toString();
    }

    protected record SseConnection(Long memberId, String connectionId, SseEmitter emitter) { }
}
