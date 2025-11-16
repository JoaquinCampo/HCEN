package grupo12.practico.messaging.ClinicalDocument.CreateDocument;

/**
 * Centralizes JMS configuration details for the create clinical document flow.
 */
public final class CreateDocumentMessaging {

    private CreateDocumentMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries create clinical document requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_create_clinical_document";

    /**
     * Field separator for the plain-text JMS payload.
     * Note: This messaging uses JSON serialization for complex DTOs.
     */
    public static final String FIELD_SEPARATOR = "|";
}

