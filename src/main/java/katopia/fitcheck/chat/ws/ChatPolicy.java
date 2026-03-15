package katopia.fitcheck.chat.ws;

public final class ChatPolicy {

    public static final String ENDPOINT = "/ws/chat";
    public static final String TOPIC_PREFIX = "/topic";
    public static final String APPLICATION_PREFIX = "/app";

    public static final String MESSAGE_MAPPING = "/chat.message";
    public static final String READ_STATE_MAPPING = "/chat.read-state";

    public static final String MESSAGE_SEND_DESTINATION = APPLICATION_PREFIX + MESSAGE_MAPPING;
    public static final String READ_STATE_SEND_DESTINATION = APPLICATION_PREFIX + READ_STATE_MAPPING;
    public static final String ROOM_MESSAGES_TOPIC_TEMPLATE = TOPIC_PREFIX + "/chat/rooms/{roomId}/messages";
    public static final String ROOM_READ_STATE_TOPIC_TEMPLATE = TOPIC_PREFIX + "/chat/rooms/{roomId}/read-state";

    private ChatPolicy() {
    }

    public static String roomMessagesTopic(String roomId) {
        return ROOM_MESSAGES_TOPIC_TEMPLATE.replace("{roomId}", roomId);
    }

    public static String roomReadStateTopic(String roomId) {
        return ROOM_READ_STATE_TOPIC_TEMPLATE.replace("{roomId}", roomId);
    }
}
