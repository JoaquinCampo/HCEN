package grupo12.practico.messaging.ClinicalHistory;

/**
 * Centralizes JMS configuration details for the clinical history request flow.
 */
public final class ClinicalHistoryMessaging {

    private ClinicalHistoryMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries clinical history requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_clinical_history_request";

    /**
     * Field separator for the plain-text JMS payload.
     * Note: This messaging uses JSON serialization for complex DTOs.
     */
    public static final String FIELD_SEPARATOR = "|";
}

