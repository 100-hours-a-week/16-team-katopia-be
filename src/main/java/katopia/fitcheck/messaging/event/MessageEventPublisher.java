package katopia.fitcheck.messaging.event;

public interface MessageEventPublisher {
    void publish(MessageEvent event);
}
