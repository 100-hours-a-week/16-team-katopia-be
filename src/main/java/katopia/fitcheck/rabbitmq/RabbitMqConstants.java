package katopia.fitcheck.rabbitmq;

public final class RabbitMqConstants {

    public static final String DOMAIN_EVENTS_EXCHANGE = "domain_events";

    public static final String NOTIFICATION_TARGET_QUEUE = "notification_target_queue";
    public static final String NOTIFICATION_BATCH_QUEUE = "notification_batch_queue";

    public static final String NOTIFICATION_META_ROUTING_KEY = "notification.meta";
    public static final String NOTIFICATION_BATCH_ROUTING_KEY = "notification.batch";

    private RabbitMqConstants() {
    }
}
