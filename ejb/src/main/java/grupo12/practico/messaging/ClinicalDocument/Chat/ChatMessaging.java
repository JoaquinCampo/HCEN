package grupo12.practico.messaging.ClinicalDocument.Chat;

/**
 * Centralizes JMS configuration details for the chat request flow.
 */
public final class ChatMessaging {

    private ChatMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries chat requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_chat_request";

    /**
     * Field separator for the plain-text JMS payload.
     * Note: This messaging uses JSON serialization for complex DTOs.
     */
    public static final String FIELD_SEPARATOR = "|";
}

