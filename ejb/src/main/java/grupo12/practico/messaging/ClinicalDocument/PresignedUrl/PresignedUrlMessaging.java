package grupo12.practico.messaging.ClinicalDocument.PresignedUrl;

/**
 * Centralizes JMS configuration details for the presigned URL request flow.
 */
public final class PresignedUrlMessaging {

    private PresignedUrlMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries presigned URL requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_presigned_url_request";

    /**
     * Field separator for the plain-text JMS payload.
     * Note: This messaging uses JSON serialization for complex DTOs.
     */
    public static final String FIELD_SEPARATOR = "|";
}

